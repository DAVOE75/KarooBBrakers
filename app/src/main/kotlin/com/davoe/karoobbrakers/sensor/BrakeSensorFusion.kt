package com.davoe.karoobbrakers.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.model.BrakeMetrics
import com.davoe.karoobbrakers.model.SurfaceProfile
import com.davoe.karoobbrakers.model.TempStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
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
    private var speedDropDecelEstimate: Double = 0.0
    private var currentCadenceRpm: Double = 0.0
    private var currentPowerWatts: Double = 0.0

    // Filter & Sensor states
    private var filteredDecelMs2: Double = 0.0
    private var previousDecelMs2: Double = 0.0
    private var gravityEstimate = FloatArray(3) { 0f }
    private var lastAccelTimestampNs: Long = 0L

    // Jerk (da/dt) calculation rolling history
    private val jerkHistorySize = 6
    private val recentDecels = DoubleArray(jerkHistorySize) { 0.0 }
    private val recentTimestampsNs = LongArray(jerkHistorySize) { 0L }
    private var historyIndex = 0
    private var historyCount = 0

    // Brake State Machine
    private var isCurrentlyBraking = false
    private var brakeBiteConfirmed = false
    private var eventStartTimeMs = 0L
    private var minBrakeHoldDurationMs = 120L // Debounce against road chatter
    private var currentEventPeakDecelMs2 = 0.0
    private var currentEventEnergyJoules = 0.0
    private var totalBrakeEventsCount = 0
    private var totalBrakingTimeAccumulatedMs = 0L
    private var totalEnergyAccumulatedKj = 0.0
    private var maxRideDecelMs2 = 0.0

    // Thermodynamic state
    private val ambientTempCelsius = 22.0
    private var currentEstimatedTempCelsius = ambientTempCelsius
    private var peakEstimatedTempCelsius = ambientTempCelsius

    // Callbacks for Karoo system alerts (beeper, banner)
    var onHardBrakeDetected: ((peakDecel: Double, gForce: Double) -> Unit)? = null
    var onThermalWarningDetected: ((brakeTypeName: String, tempCelsius: Double) -> Unit)? = null

    fun start() {
        gravityEstimate = FloatArray(3) { 0f }
        filteredDecelMs2 = 0.0
        previousDecelMs2 = 0.0
        historyIndex = 0
        historyCount = 0

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
        if (lastSpeedTimestampNs > 0L && now > lastSpeedTimestampNs) {
            val dt = (now - lastSpeedTimestampNs) / 1_000_000_000.0
            if (dt in 0.04..2.0) {
                // Kinematic acceleration (negative = speed dropping)
                val kinematicAccel = (speedMps - lastSpeedMs) / dt
                if (kinematicAccel < -0.1) {
                    speedDropDecelEstimate = -kinematicAccel
                } else {
                    speedDropDecelEstimate = 0.0
                    // If speed is accelerating significantly, cyclist cannot be braking
                    if (kinematicAccel > 0.4 && isCurrentlyBraking) {
                        endBrakeEvent()
                    }
                }
            }
        }
        lastSpeedMs = currentSpeedMs
        currentSpeedMs = max(0.0, speedMps)
        lastSpeedTimestampNs = now
    }

    fun onCadenceUpdate(cadenceRpm: Double) {
        currentCadenceRpm = max(0.0, cadenceRpm)
        // Pedaling lockout: Actively pedaling means zero braking
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
        currentEstimatedTempCelsius = ambientTempCelsius
        peakEstimatedTempCelsius = ambientTempCelsius
        isCurrentlyBraking = false
        brakeBiteConfirmed = false
        _metrics.value = BrakeMetrics()
    }

    /**
     * Estimates natural aerodynamic drag and rolling resistance deceleration (m/s²).
     * Any deceleration less than or equal to this value is pure coasting due to inertia!
     */
    private fun calculateCoastingDrag(speedMs: Double, weightKg: Double, surface: SurfaceProfile): Double {
        if (speedMs < 0.2) return 0.0

        // Rolling resistance coefficient Crr
        val crr = when (surface) {
            SurfaceProfile.ROAD -> 0.0042
            SurfaceProfile.GRAVEL -> 0.0070
            SurfaceProfile.MTB -> 0.0095
        }
        val rollingDragDecel = crr * 9.80665

        // Aerodynamic drag: Faero = 0.5 * rho * CdA * v^2
        // CdA ~ 0.35 m^2 (hoods/drops endurance), air density ~ 1.205 kg/m^3
        val rho = 1.205
        val cdA = 0.35
        val aeroForceNewtons = 0.5 * rho * cdA * speedMs.pow(2)
        val aeroDragDecel = aeroForceNewtons / weightKg.coerceAtLeast(40.0)

        return rollingDragDecel + aeroDragDecel
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
        val totalWeightKg = preferences.getTotalWeightKg().toDouble()
        val brakeType = preferences.getBrakeType()

        // 1. Decouple Dynamic Linear Deceleration from Gravity & Incline
        val rawForwardDecel: Double = if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Forward axis on standard Karoo stem mount is -Y or +Z depending on pitch
            val forwardLinear = -event.values[1].toDouble()
            forwardLinear
        } else {
            // Low-pass filter to extract static gravity tilt (incline compensation)
            val beta = 0.94f
            gravityEstimate[0] = beta * gravityEstimate[0] + (1 - beta) * event.values[0]
            gravityEstimate[1] = beta * gravityEstimate[1] + (1 - beta) * event.values[1]
            gravityEstimate[2] = beta * gravityEstimate[2] + (1 - beta) * event.values[2]

            // Dynamic deceleration along forward axis with incline tilt cancelled
            val linY = -(event.values[1] - gravityEstimate[1]).toDouble()
            linY
        }

        // 2. Pedaling & Stationary Lockout
        val isPedaling = currentCadenceRpm > 5.0 || currentPowerWatts > 25.0
        val isMoving = currentSpeedMs > 0.8 // Bike moving at least ~2.9 km/h

        val rawDecelNonNegative = if (isPedaling || !isMoving) {
            0.0
        } else {
            max(0.0, rawForwardDecel)
        }

        // 3. Two-Stage Filtering (Fast Attack, Road Chatter Rejection)
        val alpha = surfaceProfile.filterAlpha.toDouble()
        // Fast attack when deceleration increases rapidly (brake bite), smoother decay
        val effectiveAlpha = if (rawDecelNonNegative > filteredDecelMs2) min(0.40, alpha * 1.8) else alpha
        filteredDecelMs2 = effectiveAlpha * rawDecelNonNegative + (1.0 - effectiveAlpha) * filteredDecelMs2

        // 4. Coasting Inertia Drag Baseline Subtraction
        // Calculate the passive deceleration caused purely by wind resistance and tire rolling
        val coastingDragMs2 = calculateCoastingDrag(currentSpeedMs, totalWeightKg, surfaceProfile)
        val netBrakeDecelMs2 = max(0.0, filteredDecelMs2 - coastingDragMs2)

        // 5. Jerk (da/dt) Calculation: Detect Lever Touch ("Bite") vs Lever Release ("Soltar")
        recentDecels[historyIndex] = filteredDecelMs2
        recentTimestampsNs[historyIndex] = nowNs
        historyIndex = (historyIndex + 1) % jerkHistorySize
        if (historyCount < jerkHistorySize) historyCount++

        val jerkMs3: Double = if (historyCount >= 3) {
            val oldestIdx = if (historyCount < jerkHistorySize) 0 else historyIndex
            val deltaA = filteredDecelMs2 - recentDecels[oldestIdx]
            val deltaT = (nowNs - recentTimestampsNs[oldestIdx]) / 1_000_000_000.0
            if (deltaT > 0.02) deltaA / deltaT else 0.0
        } else {
            0.0
        }

        // 6. Fine-Tuned State Machine: Brake Bite vs Coasting Inertia vs Brake Release
        val nowMs = System.currentTimeMillis()

        // Threshold for initial pad contact ("toque de freno")
        val biteThreshold = when (surfaceProfile) {
            SurfaceProfile.ROAD -> 0.40   // 0.04G net above aerodynamic drag
            SurfaceProfile.GRAVEL -> 0.55 // 0.056G
            SurfaceProfile.MTB -> 0.70    // 0.071G
        }

        // Threshold for releasing brake lever ("soltar el freno")
        val releaseThreshold = biteThreshold * 0.35

        val isDecelAboveBite = netBrakeDecelMs2 >= biteThreshold
        val hasBiteJerk = jerkMs3 > 0.65 // Positive impulse on pad contact
        val isSpeedDropping = speedDropDecelEstimate > 0.15

        if (!isCurrentlyBraking) {
            // Trigger braking ONLY when:
            // - Net deceleration exceeds natural coasting drag
            // - Either sudden positive jerk (lever bite) OR confirmed GPS speed drop
            // - Cyclist is not pedaling and is actually moving
            if (isDecelAboveBite && (hasBiteJerk || isSpeedDropping) && !isPedaling && isMoving) {
                isCurrentlyBraking = true
                brakeBiteConfirmed = true
                eventStartTimeMs = nowMs
                currentEventPeakDecelMs2 = netBrakeDecelMs2
                currentEventEnergyJoules = 0.0
                totalBrakeEventsCount++
            }
        } else {
            // Cyclist is currently modulating brakes
            if (netBrakeDecelMs2 > currentEventPeakDecelMs2) {
                currentEventPeakDecelMs2 = netBrakeDecelMs2
            }
            if (currentEventPeakDecelMs2 > maxRideDecelMs2) {
                maxRideDecelMs2 = currentEventPeakDecelMs2
            }

            // Power = Force * Velocity = (m * a_net) * v
            val brakingForceNewtons = totalWeightKg * netBrakeDecelMs2
            val instantaneousPowerWatts = brakingForceNewtons * currentSpeedMs
            val deltaEnergyJoules = instantaneousPowerWatts * dt
            currentEventEnergyJoules += deltaEnergyJoules
            totalEnergyAccumulatedKj += (deltaEnergyJoules / 1000.0)

            // Emergency Hard Braking Alert
            val hardBrakeThreshold = preferences.getHardBrakeThresholdMs2().toDouble()
            if (preferences.isHardBrakeAlertEnabled() && currentEventPeakDecelMs2 >= hardBrakeThreshold) {
                onHardBrakeDetected?.invoke(currentEventPeakDecelMs2, currentEventPeakDecelMs2 / 9.80665)
            }

            // Detection of "Soltar el freno" (Brake Lever Release):
            // - Net deceleration drops below release threshold
            // - OR sharp negative jerk (jerk < -1.2 m/s³) indicating sudden lever opening
            // - OR rider resumed pedaling or came to full stop
            val hasReleaseJerk = jerkMs3 < -1.2
            val isDebounced = (nowMs - eventStartTimeMs) >= minBrakeHoldDurationMs

            val shouldRelease = isPedaling || !isMoving || (isDebounced && (netBrakeDecelMs2 < releaseThreshold || (hasReleaseJerk && netBrakeDecelMs2 < biteThreshold)))

            if (shouldRelease) {
                endBrakeEvent()
            }
        }

        previousDecelMs2 = filteredDecelMs2

        // 7. Instantaneous Braking Power
        val activeBrakeDecel = if (isCurrentlyBraking) netBrakeDecelMs2 else 0.0
        val instantaneousPowerWatts = if (isCurrentlyBraking) {
            totalWeightKg * activeBrakeDecel * currentSpeedMs
        } else {
            0.0
        }

        // 8. Thermodynamic Thermal Model (Continuous Heat Generation & Air Cooling)
        val heatInWatts = if (isCurrentlyBraking) 0.82 * instantaneousPowerWatts else 0.0
        val airSpeedMultiplier = currentSpeedMs.coerceAtLeast(0.0).pow(0.75)
        val coolingCapacity = brakeType.coolingFactorBase + (brakeType.coolingFactorVelocity * airSpeedMultiplier)
        val heatOutWatts = coolingCapacity * (currentEstimatedTempCelsius - ambientTempCelsius).coerceAtLeast(0.0)

        val dTemp = ((heatInWatts - heatOutWatts) / brakeType.thermalMassJoulePerK) * dt
        currentEstimatedTempCelsius = max(ambientTempCelsius, currentEstimatedTempCelsius + dTemp)
        if (currentEstimatedTempCelsius > peakEstimatedTempCelsius) {
            peakEstimatedTempCelsius = currentEstimatedTempCelsius
        }

        // Thermal status evaluation
        val tempStatus = when {
            currentEstimatedTempCelsius >= brakeType.criticalTempCelsius -> TempStatus.CRITICAL
            currentEstimatedTempCelsius >= brakeType.warningTempCelsius -> TempStatus.HOT
            currentEstimatedTempCelsius >= (brakeType.warningTempCelsius * 0.65) -> TempStatus.WARM
            else -> TempStatus.NORMAL
        }

        val isThermalOverheating = currentEstimatedTempCelsius >= brakeType.criticalTempCelsius
        if (preferences.isThermalWarningEnabled() && isThermalOverheating) {
            onThermalWarningDetected?.invoke(brakeType.id, currentEstimatedTempCelsius)
        }

        // 9. Differentiate Coasting (Freewheeling by inertia) from Braking
        val isCoasting = !isCurrentlyBraking && !isPedaling && isMoving

        // 10. Emit Updated Metrics
        val currentG = activeBrakeDecel / 9.80665
        val padWearScore = (totalEnergyAccumulatedKj * brakeType.wearFactor) / 10.0

        _metrics.value = BrakeMetrics(
            currentDecelMs2 = activeBrakeDecel,
            currentGForce = currentG,
            currentBrakingPowerWatts = instantaneousPowerWatts,
            isBraking = isCurrentlyBraking,
            isPedalingActive = isPedaling,
            isCoasting = isCoasting,
            peakDecelMs2 = maxRideDecelMs2,
            peakGForce = maxRideDecelMs2 / 9.80665,
            totalBrakeEvents = totalBrakeEventsCount,
            totalBrakingTimeMs = totalBrakingTimeAccumulatedMs,
            totalEnergyKj = totalEnergyAccumulatedKj,
            padWearScore = padWearScore,
            continuousBrakingEnergyKj = currentEventEnergyJoules / 1000.0,
            estimatedTempCelsius = currentEstimatedTempCelsius,
            peakTempCelsius = peakEstimatedTempCelsius,
            tempStatus = tempStatus,
            isThermalWarning = isThermalOverheating,
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
        brakeBiteConfirmed = false
        currentEventEnergyJoules = 0.0
        currentEventPeakDecelMs2 = 0.0
        eventStartTimeMs = 0L
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
