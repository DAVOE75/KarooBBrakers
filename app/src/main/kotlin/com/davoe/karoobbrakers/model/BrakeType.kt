package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

enum class BrakeType(
    val id: String,
    @StringRes val titleRes: Int,
    val wearFactor: Double,
    val maxContinuousEnergyKj: Double,
    val thermalMassJoulePerK: Double, // Heat capacity C = m * cp (J/K)
    val coolingFactorBase: Double,    // Static cooling rate (W/K)
    val coolingFactorVelocity: Double,// Convective airflow cooling multiplier
    val warningTempCelsius: Double,   // Moderate heat alert
    val criticalTempCelsius: Double,  // Dangerous overheating threshold
) {
    DISC(
        id = "DISC",
        titleRes = R.string.brake_type_disc,
        wearFactor = 1.0,
        maxContinuousEnergyKj = 35.0,
        thermalMassJoulePerK = 52.0,      // Low thermal mass rotor, heats quickly
        coolingFactorBase = 0.8,
        coolingFactorVelocity = 1.8,     // High convective cooling through rotor vents
        warningTempCelsius = 220.0,
        criticalTempCelsius = 350.0,
    ),
    RIM_ALUMINUM(
        id = "RIM_ALUMINUM",
        titleRes = R.string.brake_type_rim_alu,
        wearFactor = 1.35,
        maxContinuousEnergyKj = 22.0,
        thermalMassJoulePerK = 350.0,     // High conductivity and rim mass
        coolingFactorBase = 1.2,
        coolingFactorVelocity = 2.4,
        warningTempCelsius = 110.0,
        criticalTempCelsius = 150.0,
    ),
    RIM_CARBON(
        id = "RIM_CARBON",
        titleRes = R.string.brake_type_rim_carbon,
        wearFactor = 1.85,
        maxContinuousEnergyKj = 16.0,
        thermalMassJoulePerK = 210.0,     // Low transverse thermal conductivity, surface resin overheats
        coolingFactorBase = 0.9,
        coolingFactorVelocity = 2.0,
        warningTempCelsius = 105.0,
        criticalTempCelsius = 145.0,     // Transition temp: over 145°C risks resin softening & delamination
    );

    companion object {
        fun fromId(id: String): BrakeType = entries.firstOrNull { it.id == id } ?: DISC
    }
}
