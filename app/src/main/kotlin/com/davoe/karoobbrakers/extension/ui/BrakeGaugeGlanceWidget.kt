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
    // Determine dynamic accent color: Green -> Amber -> Bright Red
    val barColor = when {
        isPedaling -> Color(0xFF455A64) // Grayed out if pedaling
        !isBraking -> Color(0xFF263238)
        intensityPercent > 70 -> Color(0xFFFF1744) // Severe brake red
        intensityPercent > 40 -> Color(0xFFFF9100) // Moderate brake amber
        else -> Color(0xFF00E676) // Light brake green
    }

    val statusText = when {
        isPedaling -> "PEDAL"
        isBraking -> "BRAKE"
        else -> "ROLL"
    }

    val statusColor = when {
        isPedaling -> Color(0xFF78909C)
        isBraking -> Color(0xFFFF5252)
        else -> Color(0xFF00E5FF)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14))
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

        // Dynamic 5-segment Braking Intensity Meter Bar
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val segments = 5
            val filledSegments = if (isBraking) {
                ((intensityPercent / 100.0) * segments).toInt().coerceIn(1, segments)
            } else 0

            for (i in 0 until segments) {
                val isActive = i < filledSegments
                val segmentColor = if (isActive) barColor else Color(0xFF1E2530)

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
