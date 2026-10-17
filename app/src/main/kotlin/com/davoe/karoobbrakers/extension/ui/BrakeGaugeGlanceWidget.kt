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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.util.Locale

@Composable
fun BrakeGaugeGlanceWidget(
    value: Double,
    unit: String,
    intensityPercent: Int,
    isBraking: Boolean,
    isPedaling: Boolean,
    peakG: Double,
) {
    // Progressive spectrum colors from White -> Pale Yellow -> Yellow -> Orange -> Red
    val spectrumColors = listOf(
        Color(0xFFFFFFFF), // 1. Initial bite: Pure White
        Color(0xFFFFF176), // 2. Light decel: Pale Yellow
        Color(0xFFFFD600), // 3. Medium decel: Pure Yellow
        Color(0xFFFF6D00), // 4. Strong decel: Pure Orange
        Color(0xFFD50000), // 5. Severe/Emergency: Fiery Red
    )

    val statusText = when {
        isPedaling -> "PEDAL"
        isBraking -> "BRAKE"
        else -> "ARMED"
    }

    val statusColor = when {
        isPedaling -> Color(0xFF78909C)
        isBraking -> Color(0xFFFF5252)
        else -> Color(0xFF00E5FF)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF0D1017))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Top status row: Status Badge & Peak marker
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = statusText,
                style = TextStyle(
                    color = ColorProvider(statusColor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = String.format(Locale.US, "PK: %.2fG", peakG),
                style = TextStyle(
                    color = ColorProvider(Color(0xFF90A4AE)),
                    fontSize = 10.sp,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(2.dp))

        // Center big numeric display
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isBraking) String.format(Locale.US, "%.1f", value) else "0.0",
                style = TextStyle(
                    color = ColorProvider(if (isBraking) Color.White else Color(0xFF78909C)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = GlanceModifier.width(3.dp))
            Text(
                text = unit,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF00E5FF)),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        // Dynamic 5-segment Progressive Intensity Spectrum (White -> Yellow -> Orange -> Red)
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp),
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
    }
}
