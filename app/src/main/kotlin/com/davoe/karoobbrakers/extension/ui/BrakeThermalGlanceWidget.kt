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
import com.davoe.karoobbrakers.model.TempStatus
import java.util.Locale

private val DataScreenFontCondensed = FontFamily("sans-serif-condensed")

/**
 * In-Ride Dual Brake Thermal & Fade Safety Monitor Widget.
 *
 * Provides real-time heat tracking for:
 * - Front brake (horquilla/rueda delantera)
 * - Rear brake (rueda trasera)
 * - Critical material limit (carbon rim protection against resin melting & delamination)
 * - 4-stage color scale: Green (OK) -> Cyan (Warm) -> Amber (Hot) -> Neon Red (Critical!)
 */
@Composable
fun BrakeThermalGlanceWidget(
    currentTemp: Double,
    peakTemp: Double,
    criticalTemp: Double,
    unitSymbol: String,
    tempStatus: TempStatus,
    brakeTypeName: String,
    padWearScore: Double,
    frontTemp: Double = currentTemp,
    rearTemp: Double = currentTemp,
    frontTempStatus: TempStatus = tempStatus,
    rearTempStatus: TempStatus = tempStatus,
    statusText: String? = null,
    heatLabel: String = "HEAT",
    maxLabel: String = "MAX",
    frontLabel: String = "FRONT",
    rearLabel: String = "REAR",
    safetyLimitLabel: String = "Limit",
    frontTempBitmap: android.graphics.Bitmap? = null,
    rearTempBitmap: android.graphics.Bitmap? = null,
) {
    fun colorForStatus(status: TempStatus): Color = when (status) {
        TempStatus.NORMAL -> Color(0xFF00E676)   // Emerald green
        TempStatus.WARM -> Color(0xFF00E5FF)     // Cyan
        TempStatus.HOT -> Color(0xFFFF9100)      // Amber orange
        TempStatus.CRITICAL -> Color(0xFFFF1744) // Neon red alert
    }

    val overallColor = colorForStatus(tempStatus)
    val frontColor = colorForStatus(frontTempStatus)
    val rearColor = colorForStatus(rearTempStatus)

    val defaultStatusText = when (tempStatus) {
        TempStatus.NORMAL -> "OK"
        TempStatus.WARM -> "WARM"
        TempStatus.HOT -> "HOT"
        TempStatus.CRITICAL -> "CRIT!"
    }
    val finalStatusText = statusText ?: defaultStatusText

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF080B10))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // --- 1. Top Header: Status Alert & Material Safety Limit ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$heatLabel | $finalStatusText",
                style = TextStyle(
                    color = ColorProvider(overallColor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = String.format(Locale.US, "%s: %.0f%s", maxLabel, peakTemp, unitSymbol),
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFFD600)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(1.dp))

        // --- 2. Dual Channel Large Temperature Readout (FRONT & REAR) ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Front Brake Heat
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = frontLabel,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF00E5FF)),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
                if (frontTempBitmap != null) {
                    androidx.glance.Image(
                        provider = androidx.glance.ImageProvider(frontTempBitmap),
                        contentDescription = null,
                        modifier = GlanceModifier.height(20.dp),
                    )
                } else {
                    Text(
                        text = String.format(Locale.US, "%.0f%s", frontTemp, unitSymbol),
                        style = TextStyle(
                            color = ColorProvider(frontColor),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DataScreenFontCondensed,
                        ),
                    )
                }
            }

            // Vertical divider
            Box(
                modifier = GlanceModifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color(0xFF2C3440))
            ) {}

            // Rear Brake Heat
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = rearLabel,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFF9100)),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
                if (rearTempBitmap != null) {
                    androidx.glance.Image(
                        provider = androidx.glance.ImageProvider(rearTempBitmap),
                        contentDescription = null,
                        modifier = GlanceModifier.height(20.dp),
                    )
                } else {
                    Text(
                        text = String.format(Locale.US, "%.0f%s", rearTemp, unitSymbol),
                        style = TextStyle(
                            color = ColorProvider(rearColor),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DataScreenFontCondensed,
                        ),
                    )
                }
            }
        }

        Spacer(modifier = GlanceModifier.height(2.dp))

        // --- 3. Heat Zone Progress Bar (4 segments: Green -> Cyan -> Amber -> Red) ---
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val ratio = (currentTemp / criticalTemp.coerceAtLeast(1.0)).coerceIn(0.0, 1.0)
            val segments = 4
            val activeSegments = (ratio * segments).toInt().coerceIn(1, segments)

            val levelColors = listOf(
                Color(0xFF00E676), // 0-25%: Optimal
                Color(0xFF00E5FF), // 25-50%: Warm
                Color(0xFFFF9100), // 50-75%: Hot
                Color(0xFFFF1744), // 75-100%: Critical fade risk
            )

            for (i in 0 until segments) {
                val isActive = i < activeSegments
                val segColor = if (isActive) levelColors[i] else Color(0xFF1E2530)
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(5.dp)
                        .padding(horizontal = 1.dp)
                        .background(segColor)
                ) {}
            }
        }

        Spacer(modifier = GlanceModifier.height(2.dp))

        // --- 4. Sub-Info: Safety Threshold for Selected Rim/Pad Material ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = String.format(Locale.US, "%s: %.0f%s", safetyLimitLabel, criticalTemp, unitSymbol),
                style = TextStyle(
                    color = ColorProvider(if (tempStatus == TempStatus.CRITICAL) Color(0xFFFF1744) else Color(0xFF78909C)),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = brakeTypeName,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF90A4AE)),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }
    }
}
