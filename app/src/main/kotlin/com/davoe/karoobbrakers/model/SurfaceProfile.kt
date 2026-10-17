package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

enum class SurfaceProfile(
    val id: String,
    @StringRes val titleRes: Int,
    val filterAlpha: Float,
    val triggerThresholdMs2: Float,
) {
    ROAD(
        id = "ROAD",
        titleRes = R.string.surface_road,
        filterAlpha = 0.22f,
        triggerThresholdMs2 = 0.9f,
    ),
    GRAVEL(
        id = "GRAVEL",
        titleRes = R.string.surface_gravel,
        filterAlpha = 0.12f,
        triggerThresholdMs2 = 1.2f,
    ),
    MTB(
        id = "MTB",
        titleRes = R.string.surface_mtb,
        filterAlpha = 0.08f,
        triggerThresholdMs2 = 1.5f,
    );

    companion object {
        fun fromId(id: String): SurfaceProfile = entries.firstOrNull { it.id == id } ?: ROAD
    }
}
