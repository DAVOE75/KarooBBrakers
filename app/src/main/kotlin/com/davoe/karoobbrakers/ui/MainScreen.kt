package com.davoe.karoobbrakers.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davoe.karoobbrakers.R
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.model.BrakeMetrics
import com.davoe.karoobbrakers.model.BrakeType
import com.davoe.karoobbrakers.model.DecelUnit
import com.davoe.karoobbrakers.model.LeverFeel
import com.davoe.karoobbrakers.model.SurfaceProfile
import com.davoe.karoobbrakers.model.TempStatus
import com.davoe.karoobbrakers.model.TemperatureUnit
import com.davoe.karoobbrakers.model.UnitSystem
import com.davoe.karoobbrakers.model.WeightUnit
import com.davoe.karoobbrakers.ui.theme.AccentCyan
import com.davoe.karoobbrakers.ui.theme.AccentGreen
import com.davoe.karoobbrakers.ui.theme.AccentOrange
import com.davoe.karoobbrakers.ui.theme.AccentRed
import com.davoe.karoobbrakers.ui.theme.DarkCard
import com.davoe.karoobbrakers.ui.theme.DarkSurface
import com.davoe.karoobbrakers.ui.theme.TextPrimary
import com.davoe.karoobbrakers.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun MainScreen(
    preferences: BrakePreferences,
    metrics: BrakeMetrics,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_dashboard),
        stringResource(R.string.tab_screens),
        stringResource(R.string.tab_settings),
        stringResource(R.string.tab_guide),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header with Carbon Badge & Real-time State
        HeaderView(metrics = metrics)

        // 4-Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = AccentOrange,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentOrange,
                    height = 3.dp,
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) AccentOrange else TextSecondary,
                            maxLines = 1,
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            when (selectedTab) {
                0 -> TelemetryTab(preferences = preferences, metrics = metrics)
                1 -> ScreensTab(preferences = preferences, metrics = metrics)
                2 -> SettingsTab(preferences = preferences)
                3 -> GuideTab()
            }
        }
    }
}

@Composable
fun HeaderView(metrics: BrakeMetrics) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(38.dp),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = stringResource(R.string.tagline),
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 1,
            )
        }

        // Dynamic State Badge: BRAKING (Red), GRUPETA (Violet), PEDALING (Cyan), COASTING (Slate), ARMED (Green)
        val badgeColor by animateColorAsState(
            targetValue = when {
                metrics.isBraking -> AccentRed
                metrics.isGroupRidingFeathering -> Color(0xFFAB47BC)
                metrics.isPedalingActive -> AccentCyan
                metrics.isCoasting -> Color(0xFF90A4AE)
                else -> AccentGreen
            },
            label = "badgeColor"
        )
        val badgeText = when {
            metrics.isBraking -> "BRAKING"
            metrics.isGroupRidingFeathering -> "GRUPETA"
            metrics.isPedalingActive -> "PEDALING"
            metrics.isCoasting -> "COASTING"
            else -> "ARMED"
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(badgeColor.copy(alpha = 0.2f))
                .border(1.dp, badgeColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = badgeText,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ==========================================
// TAB 0: LIVE TELEMETRY & WORLDTOUR PHYSICS
// ==========================================
@Composable
fun TelemetryTab(
    preferences: BrakePreferences,
    metrics: BrakeMetrics,
) {
    val scrollState = rememberScrollState()
    val decelUnit by preferences.decelUnitFlow.collectAsState()
    val tempUnit by preferences.temperatureUnitFlow.collectAsState()

    val displayDecel = decelUnit.fromMs2(metrics.currentDecelMs2)
    val unitLabel = decelUnit.symbol
    val displayPeakDecel = decelUnit.fromMs2(metrics.peakDecelMs2)

    val spectrumColors = listOf(
        Color(0xFFFFFFFF), // 1. Pure White
        Color(0xFFFFF176), // 2. Pale Yellow
        Color(0xFFFFD600), // 3. Pure Yellow
        Color(0xFFFF6D00), // 4. Pure Orange
        Color(0xFFD50000), // 5. Fiery Red
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // 1. Live Deceleration Force Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(14.dp),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.live_test_title),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentCyan,
                )
                Text(
                    text = stringResource(R.string.live_test_desc),
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Big Deceleration Readout
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val decelColor = when {
                        metrics.isBraking -> AccentRed
                        metrics.isGroupRidingFeathering -> Color(0xFFAB47BC)
                        else -> TextPrimary
                    }
                    Text(
                        text = String.format(Locale.US, "%.2f", displayDecel),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = decelColor,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = unitLabel,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 7.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5-Segment Progressive Spectrum Meter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    val segments = 5
                    val filledSegments = if (metrics.isPedalingActive) {
                        0
                    } else if (metrics.isGroupRidingFeathering) {
                        1
                    } else if (metrics.isBraking) {
                        ((metrics.brakingIntensityPercent / 100.0) * segments).toInt().coerceIn(1, segments)
                    } else 0

                    for (i in 0 until segments) {
                        val isActive = i < filledSegments
                        val segmentColor = if (isActive) spectrumColors[i] else DarkSurface
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .background(segmentColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Real-time Status Notes
                AnimatedVisibility(visible = metrics.isGroupRidingFeathering) {
                    Text(
                        text = "PELOTON FEATHERING: Rear brake modulation active (No false stop)",
                        fontSize = 11.sp,
                        color = Color(0xFFCE93D8),
                        fontWeight = FontWeight.Bold,
                    )
                }

                AnimatedVisibility(visible = metrics.isPedalingActive) {
                    Text(
                        text = stringResource(R.string.pedaling_active),
                        fontSize = 11.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.Medium,
                    )
                }

                AnimatedVisibility(visible = metrics.isCoasting && !metrics.isPedalingActive && !metrics.isBraking) {
                    Text(
                        text = stringResource(R.string.coasting_active),
                        fontSize = 11.sp,
                        color = Color(0xFF90A4AE),
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }

        // 2. Dynamic Weight Transfer & Front/Rear Bias Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "FRONT / REAR BRAKE BALANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                    )
                    val slopeSign = if (metrics.roadSlopePercent >= 0) "+" else ""
                    Text(
                        text = "Grade: $slopeSign${String.format(Locale.US, "%.1f%%", metrics.roadSlopePercent)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (metrics.roadSlopePercent < -3f) AccentOrange else TextSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Balance Percentage Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Front: ${metrics.frontBiasPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                    )
                    Text(
                        text = "Rear: ${metrics.rearBiasPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dual Balance Proportional Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(metrics.frontBiasPercent.toFloat().coerceIn(10f, 90f))
                            .height(8.dp)
                            .background(AccentCyan)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .weight(metrics.rearBiasPercent.toFloat().coerceIn(10f, 90f))
                            .height(8.dp)
                            .background(Color(0xFFFFB74D))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Dynamic load transfer shifts up to 85% forward on steep descents or hard deceleration.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                )
            }
        }

        // 3. Kamm's Friction Circle & Cornering Grip Margin
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "KAMM'S FRICTION CIRCLE & GRIP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen,
                    )
                    val gripRiskColor = when {
                        metrics.gripMarginPercent > 50 -> AccentGreen
                        metrics.gripMarginPercent > 20 -> AccentOrange
                        else -> AccentRed
                    }
                    Text(
                        text = "${metrics.gripMarginPercent}% MARGIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = gripRiskColor,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MetricMiniBox(
                        label = "Roll Lean",
                        value = String.format(Locale.US, "%.1f°", metrics.leanAngleDeg),
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = "Lateral G",
                        value = String.format(Locale.US, "%.2f G", metrics.lateralGForce),
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = "Total G",
                        value = String.format(Locale.US, "%.2f G", metrics.totalGForce),
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Combines cornering lean with braking deceleration to monitor tire friction limits and prevent front-wheel slip.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                )
            }
        }

        // 4. Dissipated Power & Involuntary Brake Drag Detection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "KINETIC POWER & DRAG DETECTION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                    )
                    Text(
                        text = String.format(Locale.US, "%.0f W Dissipated", metrics.currentBrakingPowerWatts),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Involuntary Drag Warning
                val hasDrag = metrics.wastedDragWatts > 5f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasDrag) AccentRed.copy(alpha = 0.15f) else DarkSurface)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (hasDrag) "⚠️" else "✓",
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasDrag) {
                                "BRAKE DRAG DETECTED: ${metrics.wastedDragWatts.toInt()} W LOST"
                            } else {
                                "Zero Involuntary Drag (Pads Clean)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasDrag) AccentRed else AccentGreen,
                        )
                        Text(
                            text = if (hasDrag) "Calipers rubbing rotor or pads misaligned." else "No parasitic friction detected while pedaling.",
                            fontSize = 9.sp,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }

        // 5. Dual Thermal & Rotor Monitor Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "DUAL THERMAL ROTOR MONITOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed,
                    )
                    val brakeType = preferences.getBrakeType()
                    val criticalDisplay = tempUnit.fromCelsius(brakeType.criticalTempCelsius)
                    Text(
                        text = "Limit: ${criticalDisplay.toInt()}${tempUnit.symbol} (${brakeType.name})",
                        fontSize = 10.sp,
                        color = TextSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Front Rotor Heat Box
                    val frontTempDisp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                    val frontColor = getTempStatusColor(metrics.frontTempStatus)
                    ThermalRotorBox(
                        title = "FRONT ROTOR",
                        tempDisplay = String.format(Locale.US, "%.0f%s", frontTempDisp, tempUnit.symbol),
                        status = metrics.frontTempStatus.name,
                        statusColor = frontColor,
                        coolingNote = "Clean airflow cooling",
                        modifier = Modifier.weight(1f)
                    )

                    // Rear Rotor Heat Box
                    val rearTempDisp = tempUnit.fromCelsius(metrics.rearTempCelsius)
                    val rearColor = getTempStatusColor(metrics.rearTempStatus)
                    ThermalRotorBox(
                        title = "REAR ROTOR",
                        tempDisplay = String.format(Locale.US, "%.0f%s", rearTempDisp, tempUnit.symbol),
                        status = metrics.rearTempStatus.name,
                        statusColor = rearColor,
                        coolingNote = "Shielded chainstay",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Ride Statistics Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_max_decel),
                value = String.format(Locale.US, "%.2f %s", displayPeakDecel, unitLabel),
                subtitle = "Peak G in Ride",
                color = AccentOrange,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_brake_events),
                value = "${metrics.totalBrakeEvents}",
                subtitle = "Distinct Stops",
                color = AccentGreen,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_brake_energy),
                value = String.format(Locale.US, "%.1f kJ", metrics.totalEnergyKj),
                subtitle = "Total Absorbed",
                color = AccentCyan,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_pad_wear),
                value = String.format(Locale.US, "%.1f pts", metrics.padWearScore),
                subtitle = "Pad Wear Index",
                color = AccentRed,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ==========================================
// TAB 1: PANTALLAS DE RUTA SHOWCASE & COLOR CODE
// ==========================================
@Composable
fun ScreensTab(
    preferences: BrakePreferences,
    metrics: BrakeMetrics,
) {
    val scrollState = rememberScrollState()
    val tempUnit by preferences.temperatureUnitFlow.collectAsState()
    val decelUnit by preferences.decelUnitFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Section Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.screens_gallery_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = "High-contrast visual widgets engineered for instant legibility at 40-70 km/h.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                )
            }
        }

        // Mockup 1: Brake Gauge Widget
        WidgetMockupCard(
            title = "1. BRAKE GAUGE (GRAPHICAL)",
            dataType = "graphical_brake",
            description = "5-segment dynamic intensity meter (White -> Red), status badge, Peak G marker, and front/rear bias bar."
        ) {
            // Live Widget Render Mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "BRAKE FORCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "ARMED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(text = "2.85", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = decelUnit.symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Peak: 4.10 ${decelUnit.symbol}", fontSize = 10.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                // 5-segment bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFFFFFFFF)))
                    Box(modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFFFFF176)))
                    Box(modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFFFFD600)))
                    Box(modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFF1E2530)))
                    Box(modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFF1E2530)))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "F: 70%", fontSize = 9.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                    Text(text = "R: 30%", fontSize = 9.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Mockup 2: Dual Thermal & Fade Monitor Widget
        WidgetMockupCard(
            title = "2. DUAL THERMAL & FADE MONITOR",
            dataType = "graphical_thermal",
            description = "Individual Front & Rear rotor temperatures with 4-zone safety colors and carbon rim delamination limit warnings."
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "THERMAL MONITOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentRed)
                    Text(text = "Disc Limit: 380°C", fontSize = 9.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Front Rotor Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface)
                            .padding(8.dp),
                    ) {
                        Text(text = "FRONT ROTOR", fontSize = 9.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "118${tempUnit.symbol}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
                        Text(text = "ZONE: WARM", fontSize = 8.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                    }

                    // Rear Rotor Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface)
                            .padding(8.dp),
                    ) {
                        Text(text = "REAR ROTOR", fontSize = 9.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                        Text(text = "72${tempUnit.symbol}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AccentGreen)
                        Text(text = "ZONE: SAFE", fontSize = 8.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Mockup 3: Master Cockpit HUD Widget
        WidgetMockupCard(
            title = "3. MASTER COCKPIT HUD",
            dataType = "graphical_cockpit",
            description = "Ultimate all-in-one glance HUD: Deceleration meter, dissipated power (W), dual heat, lean angle, and grip margin %."
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "BRAKE COCKPIT HUD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                    Text(text = "GRIP: 85% SAFE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "1.92 ${decelUnit.symbol}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text(text = "340 W Dissipated", fontSize = 10.sp, color = AccentCyan, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Lean: 24°", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                        Text(text = "F: 95°C | R: 68°C", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Color Code Legend Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.screens_color_legend_title),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Decel Spectrum Items
                ColorLegendRow(Color(0xFFFFFFFF), "White", "Initial bite & light lever touch (< 1.0 m/s²)")
                ColorLegendRow(Color(0xFFFFF176), "Pale Yellow", "Subtle modulation / distance trimming (1.0 - 2.0 m/s²)")
                ColorLegendRow(Color(0xFFFFD600), "Pure Yellow", "Standard braking / corner entry (2.0 - 3.2 m/s²)")
                ColorLegendRow(Color(0xFFFF6D00), "Pure Orange", "Firm braking / steep descents (3.2 - 4.5 m/s²)")
                ColorLegendRow(Color(0xFFD50000), "Fiery Red", "Emergency braking / hard stop alarm (> 4.5 m/s²)")
                ColorLegendRow(Color(0xFFAB47BC), "Violet", "Peloton feathering mode (subtle paceline touch)")
                ColorLegendRow(AccentGreen, "Green", "Normal brake temperature (< 80°C / 176°F)")
                ColorLegendRow(AccentCyan, "Cyan", "Warm operating temperature (80 - 160°C)")
                ColorLegendRow(AccentOrange, "Amber", "Hot brake track / thermal build-up (160 - 230°C)")
                ColorLegendRow(AccentRed, "Neon Red", "Critical heat / fade risk (> 230°C or carbon limit)")
            }
        }
    }
}

@Composable
fun ColorLegendRow(color: Color, name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
                .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$name: ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = desc,
            fontSize = 10.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun WidgetMockupCard(
    title: String,
    dataType: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = dataType, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

// ==========================================
// TAB 2: CONFIGURATION & CALIBRATION
// ==========================================
@Composable
fun SettingsTab(preferences: BrakePreferences) {
    val scrollState = rememberScrollState()
    val brakeType by preferences.brakeTypeFlow.collectAsState()
    val weightKg by preferences.weightFlow.collectAsState()
    val surfaceProfile by preferences.surfaceFlow.collectAsState()
    val unitSystem by preferences.unitSystemFlow.collectAsState()
    val tempUnit by preferences.temperatureUnitFlow.collectAsState()
    val weightUnit by preferences.weightUnitFlow.collectAsState()
    val decelUnit by preferences.decelUnitFlow.collectAsState()
    val frontLeverFeel by preferences.frontLeverFeelFlow.collectAsState()
    val rearLeverFeel by preferences.rearLeverFeelFlow.collectAsState()
    val pelotonMode by preferences.pelotonModeFlow.collectAsState()
    val hardBrakeAlert by preferences.hardBrakeAlertFlow.collectAsState()
    val beeperEnabled by preferences.beeperEnabledFlow.collectAsState()
    val thermalWarning by preferences.thermalWarningFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 1. Regional Units System Preset
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_unit_system),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Text(
                    text = stringResource(R.string.desc_unit_system),
                    fontSize = 11.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val isMetric = unitSystem == UnitSystem.METRIC
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMetric) AccentCyan.copy(alpha = 0.15f) else DarkSurface)
                            .border(
                                width = if (isMetric) 1.5.dp else 1.dp,
                                color = if (isMetric) AccentCyan else Color(0xFF2C3440),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { preferences.setUnitSystem(UnitSystem.METRIC) }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Metric (EU / DE)",
                                fontSize = 12.sp,
                                fontWeight = if (isMetric) FontWeight.Bold else FontWeight.Normal,
                                color = if (isMetric) TextPrimary else TextSecondary,
                            )
                            Text(
                                text = "kg, °C, m/s²",
                                fontSize = 10.sp,
                                color = if (isMetric) AccentCyan else TextSecondary.copy(alpha = 0.7f),
                            )
                        }
                    }

                    val isImperial = unitSystem == UnitSystem.IMPERIAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isImperial) AccentOrange.copy(alpha = 0.15f) else DarkSurface)
                            .border(
                                width = if (isImperial) 1.5.dp else 1.dp,
                                color = if (isImperial) AccentOrange else Color(0xFF2C3440),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { preferences.setUnitSystem(UnitSystem.IMPERIAL) }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Imperial (UK / US)",
                                fontSize = 12.sp,
                                fontWeight = if (isImperial) FontWeight.Bold else FontWeight.Normal,
                                color = if (isImperial) TextPrimary else TextSecondary,
                            )
                            Text(
                                text = "lbs, °F, G / ft/s²",
                                fontSize = 10.sp,
                                color = if (isImperial) AccentOrange else TextSecondary.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }
        }

        // 2. Lever Free Stroke / Bite Point Calibration (Front & Rear)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "LEVER FREE STROKE & BITE POINT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = "Calibrates lever deadband and jerk threshold before pads contact rotor/rim.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Front Lever Selector
                Text(
                    text = stringResource(R.string.title_front_lever),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeverFeel.entries.forEach { feel ->
                        val isSelected = frontLeverFeel == feel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentCyan.copy(alpha = 0.15f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) AccentCyan else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setFrontLeverFeel(feel) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = feel.name.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "${feel.freeStrokeMm}mm",
                                    fontSize = 9.sp,
                                    color = if (isSelected) AccentCyan else TextSecondary.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rear Lever Selector
                Text(
                    text = stringResource(R.string.title_rear_lever),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeverFeel.entries.forEach { feel ->
                        val isSelected = rearLeverFeel == feel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFFFFB74D).copy(alpha = 0.15f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFFFB74D) else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setRearLeverFeel(feel) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = feel.name.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "${feel.freeStrokeMm}mm",
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color(0xFFFFB74D) else TextSecondary.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Peloton Feathering Mode Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingSwitchRow(
                    title = stringResource(R.string.title_peloton_mode),
                    subtitle = stringResource(R.string.desc_peloton_mode),
                    checked = pelotonMode,
                    onCheckedChange = { preferences.setPelotonModeEnabled(it) },
                )
            }
        }

        // 4. Brake System Type Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_brake_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = stringResource(R.string.desc_brake_type),
                    fontSize = 11.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                BrakeType.entries.forEach { type ->
                    val isSelected = brakeType == type
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentOrange.copy(alpha = 0.15f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) AccentOrange else Color(0xFF2C3440),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { preferences.setBrakeType(type) }
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(type.titleRes),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                )
                                val limitDisplay = tempUnit.fromCelsius(type.criticalTempCelsius)
                                Text(
                                    text = "Wear Factor: ${type.wearFactor}x | Limit: ${limitDisplay.toInt()}${tempUnit.symbol}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) AccentCyan else TextSecondary.copy(alpha = 0.7f),
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_brake_sense),
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Total System Weight Stepper & Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                val isLbs = weightUnit == WeightUnit.LBS
                val displayWeight = if (isLbs) weightKg * 2.20462262f else weightKg

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.title_total_weight),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                        )
                        Text(
                            text = stringResource(R.string.desc_total_weight),
                            fontSize = 11.sp,
                            color = TextSecondary,
                        )
                    }
                    Text(
                        text = "${displayWeight.toInt()} ${weightUnit.symbol}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentCyan,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isLbs) {
                    Slider(
                        value = displayWeight,
                        onValueChange = { preferences.setTotalWeightKg(it / 2.20462262f) },
                        valueRange = 90f..350f,
                        steps = 260,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentCyan,
                            activeTrackColor = AccentCyan,
                            inactiveTrackColor = DarkSurface,
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(
                            onClick = { preferences.setTotalWeightKg((displayWeight - 10f) / 2.20462262f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("-10 lbs", fontSize = 11.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { preferences.setTotalWeightKg(80f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("Default (175 lbs)", fontSize = 11.sp, color = AccentCyan)
                        }
                        Button(
                            onClick = { preferences.setTotalWeightKg((displayWeight + 10f) / 2.20462262f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("+10 lbs", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                } else {
                    Slider(
                        value = weightKg,
                        onValueChange = { preferences.setTotalWeightKg(it) },
                        valueRange = 40f..160f,
                        steps = 120,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentCyan,
                            activeTrackColor = AccentCyan,
                            inactiveTrackColor = DarkSurface,
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(
                            onClick = { preferences.setTotalWeightKg(weightKg - 5f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("-5 kg", fontSize = 11.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { preferences.setTotalWeightKg(80f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("Default (80 kg)", fontSize = 11.sp, color = AccentCyan)
                        }
                        Button(
                            onClick = { preferences.setTotalWeightKg(weightKg + 5f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            modifier = Modifier.height(34.dp),
                        ) {
                            Text("+5 kg", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 6. Surface Profile Selector (Including Wet Road & Friction Mu)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_surface),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )
                Text(
                    text = stringResource(R.string.desc_surface),
                    fontSize = 11.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SurfaceProfile.entries.forEach { profile ->
                        val isSelected = surfaceProfile == profile
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentGreen.copy(alpha = 0.15f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) AccentGreen else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setSurfaceProfile(profile) }
                                .padding(vertical = 8.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(profile.titleRes),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AccentGreen else TextSecondary,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "μ=${profile.frictionMu}",
                                    fontSize = 9.sp,
                                    color = if (isSelected) AccentGreen else TextSecondary.copy(alpha = 0.6f),
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Custom Unit Overrides (Temperature, Decel)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Custom Unit Preferences",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.title_temperature_unit), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Choose Celsius or Fahrenheit", fontSize = 10.sp, color = TextSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { preferences.setTemperatureUnit(TemperatureUnit.CELSIUS) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tempUnit == TemperatureUnit.CELSIUS) AccentGreen else DarkSurface
                            ),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("°C", fontSize = 11.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { preferences.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tempUnit == TemperatureUnit.FAHRENHEIT) AccentGreen else DarkSurface
                            ),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("°F", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.title_unit), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "m/s², G-Force or ft/s²", fontSize = 10.sp, color = TextSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { preferences.setDecelUnit(DecelUnit.MS2) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (decelUnit == DecelUnit.MS2) AccentCyan else DarkSurface
                            ),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("m/s²", fontSize = 10.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { preferences.setDecelUnit(DecelUnit.G_FORCE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (decelUnit == DecelUnit.G_FORCE) AccentCyan else DarkSurface
                            ),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("G", fontSize = 10.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { preferences.setDecelUnit(DecelUnit.FT_S2) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (decelUnit == DecelUnit.FT_S2) AccentCyan else DarkSurface
                            ),
                            modifier = Modifier.height(32.dp),
                        ) {
                            Text("ft/s²", fontSize = 10.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 8. Safety Alerts Switches
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingSwitchRow(
                    title = stringResource(R.string.alert_hard_braking),
                    subtitle = stringResource(R.string.desc_hard_braking),
                    checked = hardBrakeAlert,
                    onCheckedChange = { preferences.setHardBrakeAlertEnabled(it) },
                )

                SettingSwitchRow(
                    title = stringResource(R.string.alert_thermal),
                    subtitle = stringResource(R.string.desc_thermal),
                    checked = thermalWarning,
                    onCheckedChange = { preferences.setThermalWarningEnabled(it) },
                )

                SettingSwitchRow(
                    title = stringResource(R.string.alert_beeper),
                    subtitle = stringResource(R.string.desc_beeper),
                    checked = beeperEnabled,
                    onCheckedChange = { preferences.setBeeperEnabled(it) },
                )
            }
        }

        // Support Card
        BuyMeACoffeeCard()
    }
}

// ==========================================
// TAB 3: USER GUIDE & WORLDTOUR SCIENCE PRO
// ==========================================
@Composable
fun GuideTab() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Quick Setup Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to Configure on Hammerhead Karoo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.guide_step1),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step2),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step3),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step4),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
            }
        }

        // WorldTour Braking Science & Engineering Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "WorldTour Braking Physics & Science",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(8.dp))

                ScienceTopicItem(
                    title = "1. Dynamic Load Transfer & Front Bias",
                    explanation = "Under hard braking, inertial force transfers up to 85% of normal load to the front contact patch. Downhill slope further increases front load. The algorithm dynamically calculates front/rear bias in real time."
                )

                ScienceTopicItem(
                    title = "2. Kamm's Friction Circle & Washout Prevention",
                    explanation = "A tire has a finite adhesion limit (μ·N). Combined longitudinal braking and lateral cornering forces must not exceed this limit: a_comb = √(a_x² + a_y²) ≤ μ·g. Our grip margin monitor protects against catastrophic front wheel washout."
                )

                ScienceTopicItem(
                    title = "3. Dual Thermodynamics & Carbon Rim Fade",
                    explanation = "Front calipers experience clean oncoming laminar airflow (higher heat transfer coefficient h), while rear calipers are shielded in turbulent chainstay air. Carbon rim tracks cannot exceed 180°C without risking resin delamination."
                )

                ScienceTopicItem(
                    title = "4. Peloton Feathering & Involuntary Drag",
                    explanation = "Riding in pacelines requires subtle rear brake feathering to trim distance without disrupting the group. The extension isolates feathering from emergency stops and continuously detects parasitic brake rubbing (Wasted Watts)."
                )
            }
        }

        // Data Fields Catalog Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Complete Data Fields Catalog",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )
                Spacer(modifier = Modifier.height(8.dp))

                DataFieldItem("Braking Force", "Instantaneous deceleration (m/s², G or ft/s²)")
                DataFieldItem("Braking Power", "Kinetic energy dissipation rate in Watts (P = m·a·v)")
                DataFieldItem("Peak Brake G", "Maximum deceleration force recorded in ride")
                DataFieldItem("Braking Events", "Count of distinct brake applications")
                DataFieldItem("Brake Energy", "Total absorbed thermal energy in kJ")
                DataFieldItem("Pad Wear Score", "Wear index taking into account disc vs carbon rim")
                DataFieldItem("Brake Temperature", "Estimated real-time rim/rotor temperature in °C or °F")
                DataFieldItem("Brake Gauge (Graphical)", "Dynamic White to Red spectrum meter with peak marker")
                DataFieldItem("Thermal Monitor (Graphical)", "Dual Front & Rear rotor temperatures with 4-zone safety colors")
                DataFieldItem("Cockpit HUD (Graphical)", "Master cockpit telemetry HUD combining decel, power, heat, and grip")
            }
        }

        // Support Card (Buy Me a Coffee)
        BuyMeACoffeeCard()
    }
}

@Composable
fun ScienceTopicItem(title: String, explanation: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = explanation, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
    }
}

@Composable
fun ThermalRotorBox(
    title: String,
    tempDisplay: String,
    status: String,
    statusColor: Color,
    coolingNote: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(text = status, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = tempDisplay, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = coolingNote, fontSize = 9.sp, color = TextSecondary.copy(alpha = 0.7f))
    }
}

@Composable
fun MetricMiniBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, fontSize = 9.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

fun getTempStatusColor(status: TempStatus): Color {
    return when (status) {
        TempStatus.NORMAL -> AccentGreen
        TempStatus.WARM -> AccentCyan
        TempStatus.HOT -> AccentOrange
        TempStatus.CRITICAL -> AccentRed
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AccentOrange,
                checkedTrackColor = AccentOrange.copy(alpha = 0.3f),
                uncheckedThumbColor = Color(0xFF78909C),
                uncheckedTrackColor = DarkSurface,
            ),
        )
    }
}

@Composable
fun BuyMeACoffeeCard() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141820)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, Color(0xFFFFDD00).copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(text = "☕", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.title_support),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFDD00),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.desc_support),
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/hesiox"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFDD00),
                    contentColor = Color.Black,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
            ) {
                Text(
                    text = stringResource(R.string.btn_buy_coffee),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.author_credit),
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun DataFieldItem(name: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "• $name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = desc, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 10.dp))
    }
}
