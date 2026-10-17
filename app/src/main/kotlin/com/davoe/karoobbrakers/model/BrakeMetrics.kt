package com.davoe.karoobbrakers.model

enum class TempStatus {
    NORMAL,
    WARM,
    HOT,
    CRITICAL
}

data class BrakeMetrics(
    val currentDecelMs2: Double = 0.0,
    val currentGForce: Double = 0.0,
    val currentBrakingPowerWatts: Double = 0.0,
    val isBraking: Boolean = false,
    val isPedalingActive: Boolean = false,
    val isCoasting: Boolean = false,
    val peakDecelMs2: Double = 0.0,
    val peakGForce: Double = 0.0,
    val totalBrakeEvents: Int = 0,
    val totalBrakingTimeMs: Long = 0L,
    val totalEnergyKj: Double = 0.0,
    val padWearScore: Double = 0.0,
    val continuousBrakingEnergyKj: Double = 0.0,
    val estimatedTempCelsius: Double = 22.0,
    val peakTempCelsius: Double = 22.0,
    val tempStatus: TempStatus = TempStatus.NORMAL,
    val isThermalWarning: Boolean = false,
    val isHardBrakeAlert: Boolean = false,
) {
    val brakingIntensityPercent: Int
        get() = ((currentDecelMs2 / 5.0) * 100.0).coerceIn(0.0, 100.0).toInt()

    val estimatedTempFahrenheit: Double
        get() = estimatedTempCelsius * 1.8 + 32.0

    val peakTempFahrenheit: Double
        get() = peakTempCelsius * 1.8 + 32.0

    val currentDecelFtS2: Double
        get() = currentDecelMs2 * 3.2808399
}
