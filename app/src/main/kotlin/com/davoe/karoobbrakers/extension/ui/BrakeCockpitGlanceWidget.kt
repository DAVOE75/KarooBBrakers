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
 * All-In-One Integrated WorldTour Brake Cockpit HUD widget for Karoo 2 & Karoo 3.
 *
 * Provides instant in-ride visibility of:
 * - Real-time deceleration with unit (m/s², G, ft/s²)
 * - 5-Stage progressive intensity spectrum bar (White -> Pale Yellow -> Yellow -> Orange -> Red)
 * - Operational state pill: ARMED (Cyan) | BRAKE (Neon Red) | GRUPETA (Violet) | PEDAL (Slate Blue) | TRAIL (Orange)
 * - WorldTour Descent Efficiency Score (DES %) & Trail-Braking status
 * - Kinetic power dissipation (Watts) in Cyan
 * - Dual-channel Front (Cyan) vs Rear (Amber) bias and temperature readouts
 * - Pad Life Odometer % remaining
 * - Peak G-Force marker
 */
@Composable
fun BrakeCockpitGlanceWidget(
    decelValue: Double,
    decelUnit: String,
    powerWatts: Double,
    temperature: Double,
    tempUnit: String,
    tempStatus: TempStatus,
    intensityPercent: Int,
    isBraking: Boolean,
    isPedaling: Boolean,
    peakG: Double,
    eventsCount: Int,
    isGroupFeathering: Boolean = false,
    frontBias: Int = 60,
    rearBias: Int = 40,
    frontTemp: Double = temperature,
    rearTemp: Double = temperature,
    frontTempStatus: TempStatus = tempStatus,
    rearTempStatus: TempStatus = tempStatus,
    statusText: String? = null,
    hudLabel: String = "HUD",
    peakLabel: String = "PK",
    powerLabel: String = "PWR",
    frontLabel: String = "F",
    rearLabel: String = "R",
    descentEfficiencyScore: Int = 100,
    isDescending: Boolean = false,
    isTrailBraking: Boolean = false,
    padLifeRemainingPercent: Int = 100,
    decelBitmap: android.graphics.Bitmap? = null,
) {
    // 5-Stage Progressive Spectrum Colors (White -> Yellow -> Orange -> Red)
    val spectrumColors = listOf(
        Color(0xFFFFFFFF), // 1. Initial bite: Pure White
        Color(0xFFFFF176), // 2. Light decel: Pale Yellow
        Color(0xFFFFD600), // 3. Medium decel: Pure Yellow
        Color(0xFFFF6D00), // 4. Strong decel: Pure Orange
        Color(0xFFD50000), // 5. Emergency stop: Fiery Red
    )

    val defaultStatusText = when {
        isTrailBraking -> "TRAIL"
        isPedaling -> "PEDAL"
        isGroupFeathering -> "GRUPETA"
        isBraking -> "BRAKE"
        else -> "ARMED"
    }
    val finalStatusText = if (isTrailBraking) "TRAIL" else (statusText ?: defaultStatusText)
    val statusColor = when {
        isTrailBraking -> Color(0xFFFF9100)
        isPedaling -> Color(0xFF78909C)
        isGroupFeathering -> Color(0xFFB388FF)
        isBraking -> Color(0xFFFF1744)
        else -> Color(0xFF00E5FF)
    }

    fun colorForStatus(status: TempStatus): Color = when (status) {
        TempStatus.NORMAL -> Color(0xFF00E676)
        TempStatus.WARM -> Color(0xFF00E5FF)
        TempStatus.HOT -> Color(0xFFFF9100)
        TempStatus.CRITICAL -> Color(0xFFFF1744)
    }

    val frontColor = colorForStatus(frontTempStatus)
    val rearColor = colorForStatus(rearTempStatus)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF080B10))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // --- 1. Top Header Bar: Status Badge | WorldTour Indicator / Descent Score | Peak G ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$hudLabel | $finalStatusText",
                style = TextStyle(
                    color = ColorProvider(if (isBraking) statusColor else Color.White),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())

            if (isDescending) {
                Text(
                    text = "DES $descentEfficiencyScore%",
                    style = TextStyle(
                        color = ColorProvider(if (descentEfficiencyScore >= 85) Color(0xFF00E676) else Color(0xFFFF9100)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
            } else {
                Text(
                    text = "#$eventsCount",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
            }

            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = String.format(Locale.US, "%s: %.2fG", peakLabel, peakG),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(1.dp))

        // --- 2. Central High-Contrast Deceleration Readout (Barlow SemiBold 600) ---
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (decelBitmap != null) {
                androidx.glance.Image(
                    provider = androidx.glance.ImageProvider(decelBitmap),
                    contentDescription = null,
                    modifier = GlanceModifier.height(60.dp),
                )
            } else {
                Text(
                    text = if (isBraking) String.format(Locale.US, "%.1f", decelValue) else "0.0",
                    style = TextStyle(
                        color = ColorProvider(if (isBraking) Color.White else Color(0xFF78909C)),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.width(3.dp))
            Text(
                text = decelUnit,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = DataScreenFontCondensed,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(2.dp))

        // --- 3. 5-Segment Progressive Intensity Bar ---
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(5.dp),
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
                        .height(5.dp)
                        .padding(horizontal = 1.dp)
                        .background(segmentColor)
                ) {}
            }
        }

        Spacer(modifier = GlanceModifier.height(2.dp))

        // --- 4. Sub-Telemetry Row: Dissipated Power | Pad Life | Dual Heat Readout ---
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Power Dissipation (Watts)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$powerLabel: ",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
                Text(
                    text = if (isBraking) String.format(Locale.US, "%.0fW", powerWatts) else "0W",
                    style = TextStyle(
                        color = ColorProvider(if (isBraking) Color(0xFF00E5FF) else Color.White),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = DataScreenFontCondensed,
                    ),
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Dual Heat (F & R)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$frontLabel:",
                    style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = DataScreenFontCondensed),
                )
                Text(
                    text = String.format(Locale.US, "%.0f°", frontTemp),
                    style = TextStyle(color = ColorProvider(frontColor), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = DataScreenFontCondensed),
                )
                Spacer(modifier = GlanceModifier.width(5.dp))
                Text(
                    text = "$rearLabel:",
                    style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = DataScreenFontCondensed),
                )
                Text(
                    text = String.format(Locale.US, "%.0f°", rearTemp),
                    style = TextStyle(color = ColorProvider(rearColor), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = DataScreenFontCondensed),
                )
            }
        }
    }
}
