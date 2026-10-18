package com.davoe.karoobbrakers.extension.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.util.Locale

private val DataScreenFontCondensed = FontFamily("sans-serif-condensed")

/**
 * In-Ride Dynamic Braking Intensity Gauge Widget.
 *
 * Designed for high-speed instant recognition (at 40-70 km/h) using vivid color coding:
 * - Pure White -> Pale Yellow -> Pure Yellow -> Pure Orange -> Fiery Red intensity spectrum.
 * - Multi-state status pill: ARMED (Cyan) | BRAKE (Neon Red) | GRUPETA (Violet) | PEDAL (Slate Blue).
 * - Dual-channel Front (Cyan) vs Rear (Amber) bias indicator.
 * - Peak G-Force memory marker.
 */
@Composable
fun BrakeGaugeGlanceWidget(
    value: Double,
    unit: String,
    intensityPercent: Int,
    isBraking: Boolean,
    isPedaling: Boolean,
    peakG: Double,
    isGroupFeathering: Boolean = false,
    frontBias: Int = 60,
    rearBias: Int = 40,
    statusText: String? = null,
    peakLabel: String = "PK",
    frontLabel: String = "F",
    rearLabel: String = "R",
    decelBitmap: android.graphics.Bitmap? = null,
) {
    // 5-Segment Progressive Intensity Colors (White -> Yellow -> Orange -> Red)
    val spectrumColors = listOf(
        Color(0xFFFFFFFF), // 1. Initial bite: Pure White
        Color(0xFFFFF176), // 2. Light decel: Pale Yellow
        Color(0xFFFFD600), // 3. Medium decel: Pure Yellow
        Color(0xFFFF6D00), // 4. Strong decel: Pure Orange
        Color(0xFFD50000), // 5. Emergency stop: Fiery Red
    )

    val defaultStatusText = when {
        isPedaling -> "PEDAL"
        isGroupFeathering -> "GRUPETA"
        isBraking -> "BRAKE"
        else -> "ARMED"
    }
    val finalStatusText = statusText ?: defaultStatusText
    val statusColor = when {
        isPedaling -> Color(0xFF78909C)
        isGroupFeathering -> Color(0xFFB388FF)
        isBraking -> Color(0xFFFF1744)
        else -> Color(0xFF00E5FF)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF090C12))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // --- 1. Top Status Row: Badge | Peak G Marker ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = finalStatusText,
                style = TextStyle(
                    color = ColorProvider(if (isBraking) statusColor else Color.White),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = String.format(Locale.US, "%s: %.2fG", peakLabel, peakG),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(1.dp))

        // --- 2. Central High-Contrast Deceleration Readout (Barlow Condensed Bold 700) ---
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (decelBitmap != null) {
                androidx.glance.Image(
                    provider = androidx.glance.ImageProvider(decelBitmap),
                    contentDescription = null,
                    modifier = GlanceModifier.height(48.dp),
                )
            } else {
                Text(
                    text = if (isBraking) String.format(Locale.US, "%.1f", value) else "0.0",
                    style = TextStyle(
                        color = ColorProvider(if (isBraking) Color.White else Color(0xFF78909C)),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
                text = unit,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF00E5FF)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(3.dp))

        // --- 3. Dynamic 5-Segment Progressive Intensity Spectrum Bar ---
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val segments = 5
            val filledSegments = if (isPedaling) {
                0
            } else if (isBraking) {
                ((intensityPercent / 100.0) * segments).toInt().coerceIn(1, segments)
            } else 0

            for (i in 0 until segments) {
                val isActive = i < filledSegments
                val segmentColor = if (isActive) spectrumColors[i] else Color(0xFF1E2530)

                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(6.dp)
                        .padding(horizontal = 1.dp)
                        .background(segmentColor)
                ) {}
            }
        }

        Spacer(modifier = GlanceModifier.height(3.dp))

        // --- 4. Sub-Row: Dual Brake Distribution (Front vs Rear) ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$frontLabel: $frontBias%",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            // Dual mini split bar
            Row(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(3.dp)
                    .background(Color(0xFF1E2530)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(3.dp)
                        .background(if (isBraking) Color(0xFF00E5FF) else Color(0xFF2C3440))
                ) {}
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(3.dp)
                        .background(if (isBraking) Color(0xFFFF9100) else Color(0xFF2C3440))
                ) {}
            }
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "$rearLabel: $rearBias%",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }
    }
}
