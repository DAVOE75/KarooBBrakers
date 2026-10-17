package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

enum class SurfaceProfile(
    val id: String,
    @StringRes val titleRes: Int,
    val filterAlpha: Float,
    val triggerThresholdMs2: Float,
    val frictionMu: Double, // Friction coefficient for Kamm's circle grip limit
) {
    ROAD(
        id = "ROAD",
        titleRes = R.string.surface_road,
        filterAlpha = 0.22f,
        triggerThresholdMs2 = 0.9f,
        frictionMu = 0.85,
    ),
    WET_ROAD(
        id = "WET_ROAD",
        titleRes = R.string.surface_wet_road,
        filterAlpha = 0.20f,
        triggerThresholdMs2 = 0.7f,
        frictionMu = 0.48,
    ),
    GRAVEL(
        id = "GRAVEL",
        titleRes = R.string.surface_gravel,
        filterAlpha = 0.12f,
        triggerThresholdMs2 = 1.2f,
        frictionMu = 0.38,
    ),
    MTB(
        id = "MTB",
        titleRes = R.string.surface_mtb,
        filterAlpha = 0.08f,
        triggerThresholdMs2 = 1.5f,
        frictionMu = 0.52,
    );

    companion object {
        fun fromId(id: String): SurfaceProfile = entries.firstOrNull { it.id == id } ?: ROAD
    }
}
