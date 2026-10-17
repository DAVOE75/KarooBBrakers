package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

enum class BrakeType(
    val id: String,
    @StringRes val titleRes: Int,
    val wearFactor: Double,
    val maxContinuousEnergyKj: Double,
) {
    DISC(
        id = "DISC",
        titleRes = R.string.brake_type_disc,
        wearFactor = 1.0,
        maxContinuousEnergyKj = 35.0,
    ),
    RIM_ALUMINUM(
        id = "RIM_ALUMINUM",
        titleRes = R.string.brake_type_rim_alu,
        wearFactor = 1.35,
        maxContinuousEnergyKj = 22.0,
    ),
    RIM_CARBON(
        id = "RIM_CARBON",
        titleRes = R.string.brake_type_rim_carbon,
        wearFactor = 1.85,
        maxContinuousEnergyKj = 16.0,
    );

    companion object {
        fun fromId(id: String): BrakeType = entries.firstOrNull { it.id == id } ?: DISC
    }
}
