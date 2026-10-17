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
import com.davoe.karoobbrakers.model.TrailBrakeQuality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.atan2
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

    // Thermodynamic state (Dual Channel Front & Rear)
    private val ambientTempCelsius = 22.0
    private var currentEstimatedTempCelsius = ambientTempCelsius
    private var peakEstimatedTempCelsius = ambientTempCelsius
    private var currentFrontTempCelsius = ambientTempCelsius
    private var currentRearTempCelsius = ambientTempCelsius
    private var peakFrontTempCelsius = ambientTempCelsius
    private var peakRearTempCelsius = ambientTempCelsius

    // WorldTour Dynamics: Cornering, Apex & Trail Braking
    private var isCorneringActive = false
    private var currentCornerMinSpeedMs = Double.MAX_VALUE
    private var cornerEntrySpeedMs = 0.0
    private var lastApexSpeedKmh = 0.0
    private var lastExitDeltaKmh = 0.0
    private var cornerMaxLeanDeg = 0.0
    private var currentCornerBrakingIq = 95
    private var wasBrakingBeforeLean = false
    private var currentTrailBrakeQuality = TrailBrakeQuality.GOOD

    // WorldTour Descent Efficiency Score (DES)
    private var descentCleanEnergyAccum = 0.0
    private var descentDragEnergyAccum = 0.0
    private var rollingDescentEfficiency = 96.0

    // Involuntary Brake Drag tracking
    private var dragStartTimeMs = 0L
    private var isBrakeDragActive = false
    private var currentWastedDragWatts = 0.0

    // Callbacks for Karoo system alerts (beeper, banner)
    var onHardBrakeDetected: ((peakDecel: Double, gForce: Double) -> Unit)? = null
    var onThermalWarningDetected: ((brakeTypeName: String, tempCelsius: Double) -> Unit)? = null
    var onGlazingWarningDetected: ((tempCelsius: Double, durationSec: Double) -> Unit)? = null
    var onPadWearWarningDetected: ((remainingPercent: Int) -> Unit)? = null

    private var lastGlazingAlertTimeMs = 0L
    private var lastPadWearAlertTimeMs = 0L

    companion object {
        @Volatile
        var activeInstance: BrakeSensorFusion? = null
            internal set
    }

    fun start() {
        activeInstance = this
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
        if (activeInstance == this) {
            activeInstance = null
        }
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
        currentFrontTempCelsius = ambientTempCelsius
        currentRearTempCelsius = ambientTempCelsius
        peakFrontTempCelsius = ambientTempCelsius
        peakRearTempCelsius = ambientTempCelsius
        descentCleanEnergyAccum = 0.0
        descentDragEnergyAccum = 0.0
        rollingDescentEfficiency = 96.0
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

        val crr = when (surface) {
            SurfaceProfile.ROAD -> 0.0042
            SurfaceProfile.WET_ROAD -> 0.0048
            SurfaceProfile.GRAVEL -> 0.0070
            SurfaceProfile.MTB -> 0.0095
        }
        val rollingDragDecel = crr * 9.80665

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
        val padCompound = preferences.getPadCompound()

        // 1. Decouple Dynamic Linear Deceleration from Gravity & Incline
        val rawForwardDecel: Double = if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val forwardLinear = -event.values[1].toDouble()
            forwardLinear
        } else {
            val beta = 0.94f
            gravityEstimate[0] = beta * gravityEstimate[0] + (1 - beta) * event.values[0]
            gravityEstimate[1] = beta * gravityEstimate[1] + (1 - beta) * event.values[1]
            gravityEstimate[2] = beta * gravityEstimate[2] + (1 - beta) * event.values[2]

            val linY = -(event.values[1] - gravityEstimate[1]).toDouble()
            linY
        }

        // 2. Pedaling Lockout
        val isPedaling = currentCadenceRpm > 5.0 || currentPowerWatts > 25.0
        val isMoving = currentSpeedMs > 0.8 // Bike moving at least ~2.9 km/h

        val rawDecelNonNegative = if (isPedaling) {
            0.0
        } else {
            max(0.0, rawForwardDecel)
        }

        // 3. Two-Stage Filtering (Fast Attack, Road Chatter Rejection)
        val alpha = surfaceProfile.filterAlpha.toDouble()
        val effectiveAlpha = if (rawDecelNonNegative > filteredDecelMs2) min(0.40, alpha * 1.8) else alpha
        filteredDecelMs2 = effectiveAlpha * rawDecelNonNegative + (1.0 - effectiveAlpha) * filteredDecelMs2

        // 4. Coasting Inertia Drag Baseline Subtraction
        val coastingDragMs2 = if (isMoving) calculateCoastingDrag(currentSpeedMs, totalWeightKg, surfaceProfile) else 0.0
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

        // 6. Responsive State Machine: Brake Bite vs Coasting Inertia vs Brake Release
        val nowMs = System.currentTimeMillis()

        val frontLeverFeel = preferences.getFrontLeverFeel()
        val rearLeverFeel = preferences.getRearLeverFeel()
        val avgBiteFactor = (frontLeverFeel.biteThresholdFactor + rearLeverFeel.biteThresholdFactor) / 2.0
        val avgJerkSens = (frontLeverFeel.jerkSensitivityFactor + rearLeverFeel.jerkSensitivityFactor) / 2.0

        val baseBiteThreshold = when (surfaceProfile) {
            SurfaceProfile.ROAD -> 0.40
            SurfaceProfile.WET_ROAD -> 0.35
            SurfaceProfile.GRAVEL -> 0.55
            SurfaceProfile.MTB -> 0.70
        }
        val biteThreshold = baseBiteThreshold * avgBiteFactor
        val releaseThreshold = biteThreshold * 0.35
        val jerkBiteThreshold = 0.65 / avgJerkSens

        val isDecelAboveBite = netBrakeDecelMs2 >= biteThreshold
        val hasBiteJerk = jerkMs3 > jerkBiteThreshold

        if (!isCurrentlyBraking) {
            if ((isDecelAboveBite || (hasBiteJerk && netBrakeDecelMs2 >= releaseThreshold)) && !isPedaling) {
                isCurrentlyBraking = true
                brakeBiteConfirmed = true
                eventStartTimeMs = nowMs
                currentEventPeakDecelMs2 = netBrakeDecelMs2
                currentEventEnergyJoules = 0.0
                totalBrakeEventsCount++
            }
        } else {
            if (netBrakeDecelMs2 > currentEventPeakDecelMs2) {
                currentEventPeakDecelMs2 = netBrakeDecelMs2
            }
            if (currentEventPeakDecelMs2 > maxRideDecelMs2) {
                maxRideDecelMs2 = currentEventPeakDecelMs2
            }

            val effectiveSpeedMs = if (isMoving) currentSpeedMs else 0.0
            val brakingForceNewtons = totalWeightKg * netBrakeDecelMs2
            val instantaneousPowerWatts = brakingForceNewtons * effectiveSpeedMs
            val deltaEnergyJoules = instantaneousPowerWatts * dt
            currentEventEnergyJoules += deltaEnergyJoules
            totalEnergyAccumulatedKj += (deltaEnergyJoules / 1000.0)

            // Update cumulative pad odometer
            preferences.addCumulativePadEnergyKj(deltaEnergyJoules / 1000.0)

            // Emergency Hard Braking Alert
            val hardBrakeThreshold = preferences.getHardBrakeThresholdMs2().toDouble()
            if (preferences.isHardBrakeAlertEnabled() && currentEventPeakDecelMs2 >= hardBrakeThreshold) {
                onHardBrakeDetected?.invoke(currentEventPeakDecelMs2, currentEventPeakDecelMs2 / 9.80665)
            }

            val hasReleaseJerk = jerkMs3 < -1.0
            val isDebounced = (nowMs - eventStartTimeMs) >= minBrakeHoldDurationMs

            val shouldRelease = isPedaling || (isDebounced && (netBrakeDecelMs2 < releaseThreshold || (hasReleaseJerk && netBrakeDecelMs2 < biteThreshold)))

            if (shouldRelease) {
                endBrakeEvent()
            }
        }

        previousDecelMs2 = filteredDecelMs2

        // 7. Road Gradient / Slope Estimation from Longitudinal Gravity Vector
        val roadSlopePercent = ((-gravityEstimate[1] / 9.80665f).toDouble().coerceIn(-0.35, 0.35)) * 100.0
        val isDescending = roadSlopePercent < -2.0 && currentSpeedMs > 4.5

        // 8. Instantaneous Braking Deceleration & Kinetic Power
        val activeBrakeDecel = if (isCurrentlyBraking) netBrakeDecelMs2 else if (!isPedaling && netBrakeDecelMs2 > 0.15) netBrakeDecelMs2 else 0.0
        val instantaneousPowerWatts = if (isCurrentlyBraking && isMoving) {
            totalWeightKg * activeBrakeDecel * currentSpeedMs
        } else {
            0.0
        }

        // Live continuous duration while lever is engaged
        val liveBrakeDurationMs = if (isCurrentlyBraking && eventStartTimeMs > 0L) (nowMs - eventStartTimeMs) else 0L
        val liveTotalBrakingTimeMs = totalBrakingTimeAccumulatedMs + liveBrakeDurationMs

        // 9. WorldTour PRO Descent Efficiency Score (DES)
        if (isDescending && isCurrentlyBraking) {
            if (activeBrakeDecel >= 2.0 && liveBrakeDurationMs < 4500L) {
                // Clean WorldTour bite pulse
                descentCleanEnergyAccum += (instantaneousPowerWatts * dt / 1000.0)
            } else if (activeBrakeDecel in 0.2..1.2 && liveBrakeDurationMs > 4000L) {
                // Dragging / scrubbing heat penalty
                descentDragEnergyAccum += (instantaneousPowerWatts * dt / 1000.0)
            }

            val totalDescentBrakeEnergy = descentCleanEnergyAccum + descentDragEnergyAccum
            if (totalDescentBrakeEnergy > 2.0) {
                val cleanRatio = (descentCleanEnergyAccum / (descentCleanEnergyAccum + descentDragEnergyAccum * 1.6)).coerceIn(0.40, 1.0)
                val targetScore = cleanRatio * 100.0
                rollingDescentEfficiency = 0.96 * rollingDescentEfficiency + 0.04 * targetScore
            }
        }

        // 10. Dynamic Front vs Rear Brake Bias & Peloton Feathering State
        val isPelotonFeathering = preferences.isPelotonModeEnabled() &&
                isCurrentlyBraking &&
                activeBrakeDecel in 0.2..1.5 &&
                currentSpeedMs > 4.5 &&
                roadSlopePercent > -3.5

        val (frontBias, rearBias) = when {
            isPelotonFeathering -> Pair(35, 65)
            activeBrakeDecel > 2.8 -> {
                val slopeBonus = if (roadSlopePercent < -2.0) 5 else 0
                val f = (76 + slopeBonus).coerceIn(70, 88)
                Pair(f, 100 - f)
            }
            activeBrakeDecel > 1.2 -> {
                val slopeBonus = if (roadSlopePercent < -2.0) 6 else 0
                val f = (64 + slopeBonus).coerceIn(58, 80)
                Pair(f, 100 - f)
            }
            else -> Pair(55, 45)
        }

        val frontPowerWatts = instantaneousPowerWatts * (frontBias / 100.0)
        val rearPowerWatts = instantaneousPowerWatts * (rearBias / 100.0)

        // 11. Thermodynamic Thermal Model (Dual Channel Front & Rear)
        val frontHeatInWatts = if (isCurrentlyBraking) 0.82 * frontPowerWatts else 0.0
        val rearHeatInWatts = if (isCurrentlyBraking) 0.82 * rearPowerWatts else 0.0

        val airSpeedMultiplier = currentSpeedMs.coerceAtLeast(0.0).pow(0.75)
        val baseCooling = brakeType.coolingFactorBase + (brakeType.coolingFactorVelocity * airSpeedMultiplier)

        val coolingCapacityFront = baseCooling * 1.15
        val coolingCapacityRear = baseCooling * 0.85

        val heatOutFront = coolingCapacityFront * (currentFrontTempCelsius - ambientTempCelsius).coerceAtLeast(0.0)
        val heatOutRear = coolingCapacityRear * (currentRearTempCelsius - ambientTempCelsius).coerceAtLeast(0.0)

        val halfThermalMass = brakeType.thermalMassJoulePerK * 0.50
        val dTempFront = ((frontHeatInWatts - heatOutFront) / halfThermalMass) * dt
        val dTempRear = ((rearHeatInWatts - heatOutRear) / halfThermalMass) * dt

        currentFrontTempCelsius = max(ambientTempCelsius, currentFrontTempCelsius + dTempFront)
        currentRearTempCelsius = max(ambientTempCelsius, currentRearTempCelsius + dTempRear)

        if (currentFrontTempCelsius > peakFrontTempCelsius) peakFrontTempCelsius = currentFrontTempCelsius
        if (currentRearTempCelsius > peakRearTempCelsius) peakRearTempCelsius = currentRearTempCelsius

        currentEstimatedTempCelsius = max(currentFrontTempCelsius, currentRearTempCelsius)
        if (currentEstimatedTempCelsius > peakEstimatedTempCelsius) peakEstimatedTempCelsius = currentEstimatedTempCelsius

        fun evaluateTempStatus(temp: Double): TempStatus = when {
            temp >= brakeType.criticalTempCelsius -> TempStatus.CRITICAL
            temp >= brakeType.warningTempCelsius -> TempStatus.HOT
            temp >= (brakeType.warningTempCelsius * 0.65) -> TempStatus.WARM
            else -> TempStatus.NORMAL
        }

        val frontTempStatus = evaluateTempStatus(currentFrontTempCelsius)
        val rearTempStatus = evaluateTempStatus(currentRearTempCelsius)
        val overallTempStatus = evaluateTempStatus(currentEstimatedTempCelsius)

        val isThermalOverheating = currentEstimatedTempCelsius >= brakeType.criticalTempCelsius
        if (preferences.isThermalWarningEnabled() && isThermalOverheating) {
            onThermalWarningDetected?.invoke(brakeType.id, currentEstimatedTempCelsius)
        }

        // 12. Pad Glazing / Cristalización Detection
        val isPadGlazingAlert = preferences.isGlazingAlertEnabled() &&
                currentEstimatedTempCelsius >= padCompound.glazingRiskTempCelsius &&
                liveBrakeDurationMs > 22000L &&
                activeBrakeDecel in 0.2..1.3

        if (isPadGlazingAlert && (nowMs - lastGlazingAlertTimeMs > 35000L)) {
            lastGlazingAlertTimeMs = nowMs
            onGlazingWarningDetected?.invoke(currentEstimatedTempCelsius, (liveBrakeDurationMs / 1000.0))
        }

        // 13. WorldTour Cornering, Lean Angle & Kamm's Friction Circle
        val gravYZ = sqrt((gravityEstimate[1] * gravityEstimate[1] + gravityEstimate[2] * gravityEstimate[2]).toDouble())
        val leanAngleDeg = if (gravYZ > 0.1) {
            atan2(abs(gravityEstimate[0]).toDouble(), gravYZ) * (180.0 / Math.PI)
        } else 0.0

        val lateralAccelMs2 = abs(event.values[0] - gravityEstimate[0]).toDouble()
        val lateralG = lateralAccelMs2 / 9.80665

        val combinedTractionMs2 = sqrt(activeBrakeDecel * activeBrakeDecel + lateralAccelMs2 * lateralAccelMs2)
        val totalGForce = combinedTractionMs2 / 9.80665

        val maxAvailableGripMs2 = surfaceProfile.frictionMu * 9.80665
        val gripUsedPercent = ((combinedTractionMs2 / maxAvailableGripMs2) * 100.0).coerceIn(0.0, 120.0).toInt()
        val gripMarginPercent = (100 - gripUsedPercent).coerceIn(0, 100)
        val isNearGripLimit = gripUsedPercent >= 88

        // Trail Braking & Cornering Apex Tracker
        val isLeanedInCorner = leanAngleDeg > 12.0
        if (isLeanedInCorner) {
            if (!isCorneringActive) {
                isCorneringActive = true
                cornerEntrySpeedMs = currentSpeedMs
                currentCornerMinSpeedMs = currentSpeedMs
                cornerMaxLeanDeg = leanAngleDeg
                wasBrakingBeforeLean = isCurrentlyBraking

                currentTrailBrakeQuality = if (wasBrakingBeforeLean) {
                    TrailBrakeQuality.PERFECT
                } else {
                    TrailBrakeQuality.GOOD
                }
            } else {
                if (currentSpeedMs < currentCornerMinSpeedMs) currentCornerMinSpeedMs = currentSpeedMs
                if (leanAngleDeg > cornerMaxLeanDeg) cornerMaxLeanDeg = leanAngleDeg

                // Check for mid-corner panic brake
                if (leanAngleDeg > 20.0 && activeBrakeDecel > 2.6) {
                    currentTrailBrakeQuality = TrailBrakeQuality.PANIC_LEAN
                }
            }
        } else if (isCorneringActive && leanAngleDeg < 7.0) {
            lastApexSpeedKmh = currentCornerMinSpeedMs * 3.6
            val exitDelta = (currentSpeedMs - currentCornerMinSpeedMs) * 3.6
            lastExitDeltaKmh = max(0.0, exitDelta)
            isCorneringActive = false
            currentCornerMinSpeedMs = Double.MAX_VALUE
        }

        val isTrailBraking = isCurrentlyBraking && isLeanedInCorner && activeBrakeDecel in 0.3..2.4 && wasBrakingBeforeLean
        val currentBrakingIq = when (currentTrailBrakeQuality) {
            TrailBrakeQuality.PANIC_LEAN -> 50
            TrailBrakeQuality.PERFECT -> 98
            TrailBrakeQuality.GOOD -> 92
            TrailBrakeQuality.EARLY_RELEASE -> 85
            TrailBrakeQuality.NONE -> 90
        }

        // 14. Involuntary Brake Drag Detection (Wasted Watts Penalty)
        val isDraggingCandidate = isCurrentlyBraking && activeBrakeDecel in 0.15..0.65 && currentSpeedMs > 6.0 && roadSlopePercent > -3.5
        if (isDraggingCandidate) {
            if (dragStartTimeMs == 0L) dragStartTimeMs = nowMs
            if ((nowMs - dragStartTimeMs) > 2500L) {
                isBrakeDragActive = true
                currentWastedDragWatts = totalWeightKg * activeBrakeDecel * currentSpeedMs
            }
        } else {
            dragStartTimeMs = 0L
            isBrakeDragActive = false
            currentWastedDragWatts = 0.0
        }

        // 15. Differentiate Coasting from Braking
        val isCoasting = !isCurrentlyBraking && !isPedaling && isMoving

        // 16. Pad Life & Wear Metrics
        val padLifeRemainingPercent = preferences.getPadLifeRemainingPercent()
        val cumulativePadKj = preferences.getCumulativePadKj()
        val padWearScore = (totalEnergyAccumulatedKj * brakeType.wearFactor * padCompound.wearRateMultiplier) / 10.0

        if (padLifeRemainingPercent <= 12 && (nowMs - lastPadWearAlertTimeMs > 60000L)) {
            lastPadWearAlertTimeMs = nowMs
            onPadWearWarningDetected?.invoke(padLifeRemainingPercent)
        }

        // 17. Emit Updated Metrics
        val currentG = activeBrakeDecel / 9.80665

        _metrics.value = BrakeMetrics(
            currentDecelMs2 = activeBrakeDecel,
            currentGForce = currentG,
            lateralGForce = lateralG,
            totalGForce = totalGForce,
            peakDecelMs2 = maxRideDecelMs2,
            peakGForce = maxRideDecelMs2 / 9.80665,
            currentBrakingPowerWatts = instantaneousPowerWatts,
            frontPowerWatts = frontPowerWatts,
            rearPowerWatts = rearPowerWatts,
            wastedDragWatts = currentWastedDragWatts,
            isBrakeDragDetected = isBrakeDragActive,
            isBraking = isCurrentlyBraking,
            isPedalingActive = isPedaling,
            isCoasting = isCoasting,
            totalBrakeEvents = totalBrakeEventsCount,
            totalBrakingTimeMs = liveTotalBrakingTimeMs,
            activeBrakingDurationMs = liveBrakeDurationMs,
            totalEnergyKj = totalEnergyAccumulatedKj,
            padWearScore = padWearScore,
            padLifeRemainingPercent = padLifeRemainingPercent,
            cumulativePadEnergyKj = cumulativePadKj,
            continuousBrakingEnergyKj = currentEventEnergyJoules / 1000.0,
            frontBiasPercent = frontBias,
            rearBiasPercent = rearBias,
            frontTempCelsius = currentFrontTempCelsius,
            rearTempCelsius = currentRearTempCelsius,
            frontTempStatus = frontTempStatus,
            rearTempStatus = rearTempStatus,
            estimatedTempCelsius = currentEstimatedTempCelsius,
            peakTempCelsius = peakEstimatedTempCelsius,
            tempStatus = overallTempStatus,
            isThermalWarning = isThermalOverheating,
            isHardBrakeAlert = isCurrentlyBraking && currentEventPeakDecelMs2 >= preferences.getHardBrakeThresholdMs2(),
            isPadGlazingAlert = isPadGlazingAlert,
            roadSlopePercent = roadSlopePercent,
            leanAngleDeg = leanAngleDeg,
            gripUsedPercent = gripUsedPercent,
            gripMarginPercent = gripMarginPercent,
            isNearGripLimit = isNearGripLimit,
            brakingIqScore = currentBrakingIq,
            isTrailBraking = isTrailBraking,
            trailBrakeQuality = currentTrailBrakeQuality,
            isGroupRidingFeathering = isPelotonFeathering,
            apexSpeedKmh = lastApexSpeedKmh,
            exitSpeedDeltaKmh = lastExitDeltaKmh,
            descentEfficiencyScore = rollingDescentEfficiency.toInt(),
            isDescending = isDescending,
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
