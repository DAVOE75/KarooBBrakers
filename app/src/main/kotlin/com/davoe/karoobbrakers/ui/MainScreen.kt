package com.davoe.karoobbrakers.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
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
import com.davoe.karoobbrakers.model.SurfaceProfile
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

        // Tab Navigation
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
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) AccentOrange else TextSecondary,
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> TelemetryTab(preferences = preferences, metrics = metrics)
                1 -> SettingsTab(preferences = preferences)
                2 -> GuideTab()
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
                .clip(CircleShape)
                .background(Color(0xFF14171D))
                .border(1.5.dp, AccentOrange, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_brake_sense),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = stringResource(R.string.tagline),
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
            )
        }

        // Status badge: BRAKING (Red) vs PEDALING (Cyan) vs COASTING (Slate) vs ARMED (Green)
        val badgeColor by animateColorAsState(
            targetValue = when {
                metrics.isBraking -> AccentRed
                metrics.isPedalingActive -> AccentCyan
                metrics.isCoasting -> Color(0xFF90A4AE)
                else -> AccentGreen
            },
            label = "badgeColor"
        )
        val badgeText = when {
            metrics.isBraking -> "BRAKING"
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
    val displayTemp = tempUnit.fromCelsius(metrics.estimatedTempCelsius)
    val displayPeakTemp = tempUnit.fromCelsius(metrics.peakTempCelsius)

    // Progressive Spectrum Colors: White -> Pale Yellow -> Yellow -> Orange -> Red
    val spectrumColors = listOf(
        Color(0xFFFFFFFF), // 1. Initial bite: Pure White
        Color(0xFFFFF176), // 2. Light decel: Pale Yellow
        Color(0xFFFFD600), // 3. Medium decel: Pure Yellow
        Color(0xFFFF6D00), // 4. Strong decel: Pure Orange
        Color(0xFFD50000), // 5. Emergency: Fiery Red
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Main Live Braking Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(14.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
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
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Big Decel Readout
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = String.format(Locale.US, "%.2f", displayDecel),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (metrics.isBraking) AccentRed else TextPrimary,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = unitLabel,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5-Segment Dynamic Intensity Spectrum Bar: White -> Yellow -> Orange -> Red
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

                // Dynamic Status Notes
                AnimatedVisibility(visible = metrics.isPedalingActive) {
                    Text(
                        text = stringResource(R.string.pedaling_active),
                        fontSize = 11.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.Medium,
                    )
                }

                AnimatedVisibility(visible = metrics.isCoasting) {
                    Text(
                        text = stringResource(R.string.coasting_active),
                        fontSize = 11.sp,
                        color = Color(0xFF90A4AE),
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }

        // Metrics 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_braking_power),
                value = String.format(Locale.US, "%.0f W", metrics.currentBrakingPowerWatts),
                subtitle = "Dissipated Power",
                color = AccentCyan,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_max_decel),
                value = String.format(Locale.US, "%.2f %s", displayPeakDecel, unitLabel),
                subtitle = "Peak Decel in Ride",
                color = AccentOrange,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_brake_events),
                value = "${metrics.totalBrakeEvents}",
                subtitle = "Total Brake Stops",
                color = AccentGreen,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_brake_energy),
                value = String.format(Locale.US, "%.1f kJ", metrics.totalEnergyKj),
                subtitle = "Absorbed Energy",
                color = AccentRed,
                modifier = Modifier.weight(1f),
            )
        }

        // Pad Wear Estimation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_wear),
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.type_pad_wear),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Score based on brake material & kinetic energy",
                        fontSize = 11.sp,
                        color = TextSecondary,
                    )
                }
                Text(
                    text = String.format(Locale.US, "%.1f pts", metrics.padWearScore),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
            }
        }

        // Real-Time Brake Temperature Card (Critical for Carbon Rims)
        val tempColor = when (metrics.tempStatus) {
            com.davoe.karoobbrakers.model.TempStatus.NORMAL -> AccentGreen
            com.davoe.karoobbrakers.model.TempStatus.WARM -> AccentCyan
            com.davoe.karoobbrakers.model.TempStatus.HOT -> AccentOrange
            com.davoe.karoobbrakers.model.TempStatus.CRITICAL -> AccentRed
        }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_wear),
                            contentDescription = null,
                            tint = tempColor,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.type_brake_temperature),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tempColor.copy(alpha = 0.2f))
                            .border(1.dp, tempColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = metrics.tempStatus.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tempColor,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column {
                        Text(
                            text = String.format(Locale.US, "%.0f%s", displayTemp, tempUnit.symbol),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tempColor,
                        )
                        Text(
                            text = String.format(Locale.US, "Peak: %.0f%s", displayPeakTemp, tempUnit.symbol),
                            fontSize = 11.sp,
                            color = TextSecondary,
                        )
                    }

                    val brakeType = preferences.getBrakeType()
                    val criticalDisplay = tempUnit.fromCelsius(brakeType.criticalTempCelsius)
                    Text(
                        text = "Limit: ${criticalDisplay.toInt()}${tempUnit.symbol} (${brakeType.name})",
                        fontSize = 10.sp,
                        color = if (metrics.tempStatus == com.davoe.karoobbrakers.model.TempStatus.CRITICAL) AccentRed else TextSecondary,
                        fontWeight = if (metrics.tempStatus == com.davoe.karoobbrakers.model.TempStatus.CRITICAL) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
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
                fontSize = 20.sp,
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
fun SettingsTab(preferences: BrakePreferences) {
    val scrollState = rememberScrollState()
    val brakeType by preferences.brakeTypeFlow.collectAsState()
    val weightKg by preferences.weightFlow.collectAsState()
    val surfaceProfile by preferences.surfaceFlow.collectAsState()
    val unitSystem by preferences.unitSystemFlow.collectAsState()
    val tempUnit by preferences.temperatureUnitFlow.collectAsState()
    val weightUnit by preferences.weightUnitFlow.collectAsState()
    val decelUnit by preferences.decelUnitFlow.collectAsState()
    val hardBrakeAlert by preferences.hardBrakeAlertFlow.collectAsState()
    val beeperEnabled by preferences.beeperEnabledFlow.collectAsState()
    val thermalWarning by preferences.thermalWarningFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 1. Regional Units System Preset (UK, USA, Germany, Spain, Europe)
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
                    // Metric Button
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

                    // Imperial Button
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

        // 2. Brake Type Selector
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

        // 3. Total Weight Stepper & Slider (Adaptive for kg or lbs)
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
                            onClick = { preferences.setTotalWeightKg(80f) }, // ~176 lbs
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

        // 4. Detailed Unit Overrides (Temperature, Weight, Deceleration)
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

                // Temperature Unit Toggle
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

                // Deceleration Unit Selector
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

        // 5. Surface Profile Selector
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
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(profile.titleRes),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AccentGreen else TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        // 6. Pedaling & Incline Logic Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_pedaling_filter),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.desc_pedaling_filter),
                    fontSize = 11.sp,
                    color = TextSecondary,
                )
            }
        }

        // 7. Safety Alert Switches
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Hard Braking Alarm
                SettingSwitchRow(
                    title = stringResource(R.string.alert_hard_braking),
                    subtitle = stringResource(R.string.desc_hard_braking),
                    checked = hardBrakeAlert,
                    onCheckedChange = { preferences.setHardBrakeAlertEnabled(it) },
                )

                // Thermal Warning
                SettingSwitchRow(
                    title = stringResource(R.string.alert_thermal),
                    subtitle = stringResource(R.string.desc_thermal),
                    checked = thermalWarning,
                    onCheckedChange = { preferences.setThermalWarningEnabled(it) },
                )

                // Beeper
                SettingSwitchRow(
                    title = stringResource(R.string.alert_beeper),
                    subtitle = stringResource(R.string.desc_beeper),
                    checked = beeperEnabled,
                    onCheckedChange = { preferences.setBeeperEnabled(it) },
                )
            }
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
fun GuideTab() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to use Karoo BBrakers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.guide_step1),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step2),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step3),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step4),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Available Data Fields",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(8.dp))

                DataFieldItem("Braking Force", "Instantaneous deceleration (m/s², G or ft/s²)")
                DataFieldItem("Braking Power", "Kinetic energy dissipation rate in Watts (P = m·a_net·v)")
                DataFieldItem("Peak Brake G", "Maximum braking force recorded in ride")
                DataFieldItem("Braking Events", "Count of distinct brake applications")
                DataFieldItem("Brake Energy", "Total absorbed thermal energy in kJ")
                DataFieldItem("Pad Wear Score", "Wear index taking into account disc vs carbon rim")
                DataFieldItem("Brake Temperature", "Estimated real-time rim/rotor temperature in °C or °F with carbon overheat alert")
                DataFieldItem("Brake Gauge (Graphical)", "Dynamic White to Red spectrum meter with peak marker")
            }
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
