package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

enum class PadCompound(
    val id: String,
    @StringRes val titleRes: Int,
    val ratedEnergyCapacityKj: Double, // Cumulative lifetime friction energy in kJ
    val wearRateMultiplier: Double,    // Relative wear speed
    val glazingRiskTempCelsius: Double,// Temperature where dragging risks glazing
) {
    RESIN(
        id = "RESIN",
        titleRes = R.string.pad_compound_resin,
        ratedEnergyCapacityKj = 2500.0,
        wearRateMultiplier = 1.0,
        glazingRiskTempCelsius = 135.0,
    ),
    SEMI_METALLIC(
        id = "SEMI_METALLIC",
        titleRes = R.string.pad_compound_semi_metallic,
        ratedEnergyCapacityKj = 4500.0,
        wearRateMultiplier = 0.65,
        glazingRiskTempCelsius = 175.0,
    ),
    METALLIC(
        id = "METALLIC",
        titleRes = R.string.pad_compound_metallic,
        ratedEnergyCapacityKj = 7000.0,
        wearRateMultiplier = 0.40,
        glazingRiskTempCelsius = 220.0,
    );

    val ratedEnergyKj: Double get() = ratedEnergyCapacityKj

    companion object {
        fun fromId(id: String): PadCompound = entries.firstOrNull { it.id == id } ?: RESIN
    }
}

enum class TrailBrakeQuality(@StringRes val titleRes: Int) {
    NONE(R.string.trail_quality_none),
    PERFECT(R.string.trail_quality_perfect),
    GOOD(R.string.trail_quality_good),
    EARLY_RELEASE(R.string.trail_quality_early),
    PANIC_LEAN(R.string.trail_quality_panic),
}
