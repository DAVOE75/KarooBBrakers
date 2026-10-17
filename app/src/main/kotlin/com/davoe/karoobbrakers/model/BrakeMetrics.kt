package com.davoe.karoobbrakers.model

data class BrakeMetrics(
    val currentDecelMs2: Double = 0.0,
    val currentGForce: Double = 0.0,
    val currentBrakingPowerWatts: Double = 0.0,
    val isBraking: Boolean = false,
    val isPedalingActive: Boolean = false,
    val peakDecelMs2: Double = 0.0,
    val peakGForce: Double = 0.0,
    val totalBrakeEvents: Int = 0,
    val totalBrakingTimeMs: Long = 0L,
    val totalEnergyKj: Double = 0.0,
    val padWearScore: Double = 0.0,
    val continuousBrakingEnergyKj: Double = 0.0,
    val isThermalWarning: Boolean = false,
    val isHardBrakeAlert: Boolean = false,
) {
    val brakingIntensityPercent: Int
        get() = ((currentDecelMs2 / 5.0) * 100.0).coerceIn(0.0, 100.0).toInt()
}
