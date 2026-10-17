package com.davoe.karoobbrakers.model

import androidx.annotation.StringRes
import com.davoe.karoobbrakers.R

/**
 * Represents the lever free stroke / bite point travel calibration.
 *
 * Direct/Short (5-12mm): Quick bite, race feel, high jerk sensitivity.
 * Standard (15-20mm): Factory balanced Shimano/SRAM feel.
 * Progressive/Long (25-35mm): Soft modulation, forgiving, ideal for peloton feathering & rear brake.
 */
enum class LeverFeel(
    val id: String,
    @StringRes val titleRes: Int,
    val freeStrokeMm: Int,
    val biteThresholdFactor: Double,
    val jerkSensitivityFactor: Double,
) {
    DIRECT_SHORT("direct_short", R.string.lever_feel_direct, 8, 0.85, 1.30),
    STANDARD("standard", R.string.lever_feel_standard, 18, 1.00, 1.00),
    PROGRESSIVE_LONG("progressive_long", R.string.lever_feel_progressive, 28, 1.25, 0.75);

    companion object {
        fun fromId(id: String): LeverFeel = entries.firstOrNull { it.id == id } ?: STANDARD
    }
}
