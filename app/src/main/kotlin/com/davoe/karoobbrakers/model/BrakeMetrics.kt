package com.davoe.karoobbrakers.model

enum class TempStatus {
    NORMAL,
    WARM,
    HOT,
    CRITICAL
}

/**
 * WorldTour Level Braking & Cornering Telemetry Metrics.
 */
data class BrakeMetrics(
    // 1. Deceleration & Forces
    val currentDecelMs2: Double = 0.0,
    val currentGForce: Double = 0.0,
    val lateralGForce: Double = 0.0,
    val totalGForce: Double = 0.0,
    val peakDecelMs2: Double = 0.0,
    val peakGForce: Double = 0.0,

    // 2. Power & Energy Dissipation
    val currentBrakingPowerWatts: Double = 0.0,
    val frontPowerWatts: Double = 0.0,
    val rearPowerWatts: Double = 0.0,
    val wastedDragWatts: Double = 0.0,
    val isBrakeDragDetected: Boolean = false,
    val totalEnergyKj: Double = 0.0,
    val continuousBrakingEnergyKj: Double = 0.0,
    val padWearScore: Double = 0.0,
    val padLifeRemainingPercent: Int = 100,
    val cumulativePadEnergyKj: Double = 0.0,

    // 3. Operational States
    val isBraking: Boolean = false,
    val isPedalingActive: Boolean = false,
    val isCoasting: Boolean = false,
    val totalBrakeEvents: Int = 0,
    val totalBrakingTimeMs: Long = 0L,
    val activeBrakingDurationMs: Long = 0L,

    // 4. Dual-Channel Front & Rear Distribution
    val frontBiasPercent: Int = 60,
    val rearBiasPercent: Int = 40,
    val frontTempCelsius: Double = 22.0,
    val rearTempCelsius: Double = 22.0,
    val frontTempStatus: TempStatus = TempStatus.NORMAL,
    val rearTempStatus: TempStatus = TempStatus.NORMAL,

    // 5. Thermal Overall State
    val estimatedTempCelsius: Double = 22.0,
    val peakTempCelsius: Double = 22.0,
    val tempStatus: TempStatus = TempStatus.NORMAL,
    val isThermalWarning: Boolean = false,
    val isHardBrakeAlert: Boolean = false,
    val isPadGlazingAlert: Boolean = false,

    // 6. WorldTour Cornering, Grip & Lean Angle Dynamics
    val roadSlopePercent: Double = 0.0,
    val leanAngleDeg: Double = 0.0,
    val gripUsedPercent: Int = 0,
    val gripMarginPercent: Int = 100,
    val isNearGripLimit: Boolean = false,
    val brakingIqScore: Int = 95,
    val isTrailBraking: Boolean = false,
    val trailBrakeQuality: TrailBrakeQuality = TrailBrakeQuality.GOOD,
    val isGroupRidingFeathering: Boolean = false,
    val apexSpeedKmh: Double = 0.0,
    val exitSpeedDeltaKmh: Double = 0.0,

    // 7. WorldTour PRO Descent Efficiency Score (DES)
    val descentEfficiencyScore: Int = 100,
    val isDescending: Boolean = false,
) {
    val brakingIntensityPercent: Int
        get() = ((currentDecelMs2 / 5.0) * 100.0).coerceIn(0.0, 100.0).toInt()

    val estimatedTempFahrenheit: Double
        get() = estimatedTempCelsius * 1.8 + 32.0

    val peakTempFahrenheit: Double
        get() = peakTempCelsius * 1.8 + 32.0

    val frontTempFahrenheit: Double
        get() = frontTempCelsius * 1.8 + 32.0

    val rearTempFahrenheit: Double
        get() = rearTempCelsius * 1.8 + 32.0

    val currentDecelFtS2: Double
        get() = currentDecelMs2 * 3.2808399
}
