package com.davoe.karoobbrakers.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.model.BrakeMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class BrakeSensorFusion(
    context: Context,
    private val preferences: BrakePreferences,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val linearAccelSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val rawAccelSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _metrics = MutableStateFlow(BrakeMetrics())
    val metrics: StateFlow<BrakeMetrics> = _metrics.asStateFlow()

    // Telemetry from Karoo OS
    private var currentSpeedMs: Double = 0.0
    private var lastSpeedMs: Double = 0.0
    private var lastSpeedTimestampNs: Long = 0L
    private var currentCadenceRpm: Double = 0.0
    private var currentPowerWatts: Double = 0.0

    // Filter states
    private var filteredDecelMs2: Double = 0.0
    private var gravityEstimate = FloatArray(3) { 0f }
    private var lastAccelTimestampNs: Long = 0L

    // Event tracking
    private var isCurrentlyBraking = false
    private var currentEventPeakDecelMs2 = 0.0
    private var currentEventEnergyJoules = 0.0
    private var eventStartTimeMs = 0L
    private var totalBrakeEventsCount = 0
    private var totalBrakingTimeAccumulatedMs = 0L
    private var totalEnergyAccumulatedKj = 0.0
    private var maxRideDecelMs2 = 0.0

    // Callbacks for Karoo system alerts (beeper, banner)
    var onHardBrakeDetected: ((peakDecel: Double, gForce: Double) -> Unit)? = null
    var onThermalWarningDetected: ((brakeTypeName: String, energyKj: Double) -> Unit)? = null

    fun start() {
        gravityEstimate = FloatArray(3) { 0f }
        filteredDecelMs2 = 0.0

        if (linearAccelSensor != null) {
            sensorManager?.registerListener(this, linearAccelSensor, SensorManager.SENSOR_DELAY_GAME)
        } else if (rawAccelSensor != null) {
            sensorManager?.registerListener(this, rawAccelSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    fun onSpeedUpdate(speedMps: Double) {
        val now = System.nanoTime()
        if (lastSpeedTimestampNs > 0 && now > lastSpeedTimestampNs) {
            val dt = (now - lastSpeedTimestampNs) / 1_000_000_000.0
            if (dt in 0.05..2.0) {
                // Kinematic acceleration from speed derivative
                val kinematicAccel = (speedMps - lastSpeedMs) / dt
                // If speed is increasing significantly, cyclist is speeding up
                if (kinematicAccel > 0.5) {
                    filteredDecelMs2 = 0.0
                }
            }
        }
        lastSpeedMs = currentSpeedMs
        currentSpeedMs = max(0.0, speedMps)
        lastSpeedTimestampNs = now
    }

    fun onCadenceUpdate(cadenceRpm: Double) {
        currentCadenceRpm = max(0.0, cadenceRpm)
        // Pedaling lockout: If pedaling, force decel to 0
        if (currentCadenceRpm > 5.0 && isCurrentlyBraking) {
            endBrakeEvent()
        }
    }

    fun onPowerUpdate(powerWatts: Double) {
        currentPowerWatts = max(0.0, powerWatts)
        if (currentPowerWatts > 25.0 && isCurrentlyBraking) {
            endBrakeEvent()
        }
    }

    fun resetRideMetrics() {
        totalBrakeEventsCount = 0
        totalBrakingTimeAccumulatedMs = 0L
        totalEnergyAccumulatedKj = 0.0
        maxRideDecelMs2 = 0.0
        currentEventEnergyJoules = 0.0
        _metrics.value = BrakeMetrics()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val nowNs = event.timestamp
        val dt = if (lastAccelTimestampNs > 0L) {
            (nowNs - lastAccelTimestampNs) / 1_000_000_000.0
        } else {
            0.02
        }.coerceIn(0.005, 0.1)
        lastAccelTimestampNs = nowNs

        val surfaceProfile = preferences.getSurfaceProfile()
        val alpha = surfaceProfile.filterAlpha.toDouble()
        val triggerThreshold = surfaceProfile.triggerThresholdMs2.toDouble()
        val totalWeightKg = preferences.getTotalWeightKg().toDouble()
        val brakeType = preferences.getBrakeType()

        // 1. Compute raw linear acceleration
        val rawForwardDecel: Double = if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Y-axis aligns with device forward/backward, Z-axis vertical, X-axis lateral
            // Squeezing brakes causes negative Y acceleration (or magnitude when braking)
            // Karoo can be mounted tilted, so we inspect forward vector magnitude
            val yAccel = -event.values[1].toDouble()
            val totalLinMag = sqrt(
                event.values[0] * event.values[0] +
                event.values[1] * event.values[1] +
                event.values[2] * event.values[2]
            ).toDouble()
            // When moving forward (speed > 1 m/s), deceleration opposes motion
            if (yAccel > 0) yAccel else (totalLinMag * 0.7)
        } else {
            // High-pass filter to remove gravity component
            val beta = 0.8f
            gravityEstimate[0] = beta * gravityEstimate[0] + (1 - beta) * event.values[0]
            gravityEstimate[1] = beta * gravityEstimate[1] + (1 - beta) * event.values[1]
            gravityEstimate[2] = beta * gravityEstimate[2] + (1 - beta) * event.values[2]

            val linY = -(event.values[1] - gravityEstimate[1]).toDouble()
            max(0.0, linY)
        }

        // 2. Pedaling & Ramp Rejection Check:
        // If pedaling cadence > 5 RPM or power > 25W, or speed < 0.5 m/s, cyclist is NOT braking
        val isPedaling = currentCadenceRpm > 5.0 || currentPowerWatts > 25.0
        val isMoving = currentSpeedMs > 0.6

        val effectiveDecelInput = if (isPedaling || !isMoving) {
            0.0
        } else {
            rawForwardDecel
        }

        // 3. Low-Pass Exponential Smoothing (Rejects cobblestone / road vibration spikes)
        filteredDecelMs2 = alpha * effectiveDecelInput + (1.0 - alpha) * filteredDecelMs2
        if (filteredDecelMs2 < 0.15) filteredDecelMs2 = 0.0

        // 4. Braking State Machine
        val nowMs = System.currentTimeMillis()
        val isDecelAboveThreshold = filteredDecelMs2 >= triggerThreshold

        if (!isCurrentlyBraking && isDecelAboveThreshold && !isPedaling && isMoving) {
            // Start of a valid braking event
            isCurrentlyBraking = true
            eventStartTimeMs = nowMs
            currentEventPeakDecelMs2 = filteredDecelMs2
            currentEventEnergyJoules = 0.0
            totalBrakeEventsCount++
        } else if (isCurrentlyBraking) {
            if (filteredDecelMs2 > currentEventPeakDecelMs2) {
                currentEventPeakDecelMs2 = filteredDecelMs2
            }
            if (currentEventPeakDecelMs2 > maxRideDecelMs2) {
                maxRideDecelMs2 = currentEventPeakDecelMs2
            }

            // Power = Force * Velocity = (m * a) * v
            val brakingForceNewtons = totalWeightKg * filteredDecelMs2
            val brakingPowerWatts = brakingForceNewtons * currentSpeedMs
            val deltaEnergyJoules = brakingPowerWatts * dt
            currentEventEnergyJoules += deltaEnergyJoules
            totalEnergyAccumulatedKj += (deltaEnergyJoules / 1000.0)

            // Safety alerts check:
            // Emergency / Hard Braking Check
            val hardBrakeThreshold = preferences.getHardBrakeThresholdMs2().toDouble()
            if (preferences.isHardBrakeAlertEnabled() && currentEventPeakDecelMs2 >= hardBrakeThreshold) {
                onHardBrakeDetected?.invoke(currentEventPeakDecelMs2, currentEventPeakDecelMs2 / 9.80665)
            }

            // Overheating warning check for sustained heavy descents
            val currentEventEnergyKj = currentEventEnergyJoules / 1000.0
            if (preferences.isThermalWarningEnabled() && currentEventEnergyKj >= brakeType.maxContinuousEnergyKj) {
                onThermalWarningDetected?.invoke(brakeType.name, currentEventEnergyKj)
            }

            // Exit conditions
            if (isPedaling || filteredDecelMs2 < (triggerThreshold * 0.4) || !isMoving) {
                endBrakeEvent()
            }
        }

        // 5. Emit updated metrics
        val currentG = filteredDecelMs2 / 9.80665
        val instantaneousPowerWatts = if (isCurrentlyBraking) {
            (totalWeightKg * filteredDecelMs2 * currentSpeedMs)
        } else {
            0.0
        }
        val padWearScore = (totalEnergyAccumulatedKj * brakeType.wearFactor) / 10.0

        _metrics.value = BrakeMetrics(
            currentDecelMs2 = if (isCurrentlyBraking) filteredDecelMs2 else 0.0,
            currentGForce = if (isCurrentlyBraking) currentG else 0.0,
            currentBrakingPowerWatts = instantaneousPowerWatts,
            isBraking = isCurrentlyBraking,
            isPedalingActive = isPedaling,
            peakDecelMs2 = maxRideDecelMs2,
            peakGForce = maxRideDecelMs2 / 9.80665,
            totalBrakeEvents = totalBrakeEventsCount,
            totalBrakingTimeMs = totalBrakingTimeAccumulatedMs,
            totalEnergyKj = totalEnergyAccumulatedKj,
            padWearScore = padWearScore,
            continuousBrakingEnergyKj = currentEventEnergyJoules / 1000.0,
            isThermalWarning = currentEventEnergyJoules / 1000.0 >= brakeType.maxContinuousEnergyKj,
            isHardBrakeAlert = isCurrentlyBraking && currentEventPeakDecelMs2 >= preferences.getHardBrakeThresholdMs2(),
        )
    }

    private fun endBrakeEvent() {
        if (!isCurrentlyBraking) return
        val nowMs = System.currentTimeMillis()
        if (eventStartTimeMs > 0L) {
            totalBrakingTimeAccumulatedMs += (nowMs - eventStartTimeMs)
        }
        isCurrentlyBraking = false
        currentEventEnergyJoules = 0.0
        currentEventPeakDecelMs2 = 0.0
        eventStartTimeMs = 0L
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
