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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davoe.karoobbrakers.R
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.model.BrakeMetrics
import com.davoe.karoobbrakers.model.BrakeType
import com.davoe.karoobbrakers.model.DecelUnit
import com.davoe.karoobbrakers.model.LeverFeel
import com.davoe.karoobbrakers.model.PadCompound
import com.davoe.karoobbrakers.model.SurfaceProfile
import com.davoe.karoobbrakers.model.TempStatus
import com.davoe.karoobbrakers.model.TemperatureUnit
import com.davoe.karoobbrakers.model.TrailBrakeQuality
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
        stringResource(R.string.tab_live),
        stringResource(R.string.tab_screens),
        stringResource(R.string.tab_settings),
        stringResource(R.string.tab_guide),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header with Shield Emblem & Dynamic State
        HeaderView(metrics = metrics)

        // 4 Clean Tabs (concise, non-truncating in 480px width)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AccentOrange.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) AccentOrange else Color(0xFF262D38),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(vertical = 7.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) AccentOrange else TextSecondary,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
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
            .padding(horizontal = 10.dp, vertical = 8.dp),
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

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.tagline),
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // State Badge
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
            metrics.isBraking -> stringResource(R.string.status_braking)
            metrics.isGroupRidingFeathering -> stringResource(R.string.status_grupeta)
            metrics.isPedalingActive -> stringResource(R.string.status_pedaling)
            metrics.isCoasting -> stringResource(R.string.status_coasting)
            else -> stringResource(R.string.status_armed)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(badgeColor.copy(alpha = 0.2f))
                .border(1.dp, badgeColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = badgeText,
                color = badgeColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 1. Live Deceleration Force Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.live_test_title),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Text(
                    text = stringResource(R.string.live_test_desc),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Big Decel Readout
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
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = decelColor,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unitLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 5-Segment Progressive Spectrum Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
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
                                .height(8.dp)
                                .background(segmentColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Real-time Status Notes
                AnimatedVisibility(visible = metrics.isGroupRidingFeathering) {
                    Text(
                        text = stringResource(R.string.peloton_feathering_active),
                        fontSize = 11.5.sp,
                        color = Color(0xFFCE93D8),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                AnimatedVisibility(visible = metrics.isPedalingActive) {
                    Text(
                        text = stringResource(R.string.pedaling_active),
                        fontSize = 11.5.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                AnimatedVisibility(visible = metrics.isCoasting && !metrics.isPedalingActive && !metrics.isBraking) {
                    Text(
                        text = stringResource(R.string.coasting_active),
                        fontSize = 11.5.sp,
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
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_front_rear_balance),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val slopeSign = if (metrics.roadSlopePercent >= 0) "+" else ""
                    Text(
                        text = "${stringResource(R.string.label_road_slope)}: $slopeSign${String.format(Locale.US, "%.1f%%", metrics.roadSlopePercent)}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (metrics.roadSlopePercent < -3f) AccentOrange else TextSecondary,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${stringResource(R.string.label_front)}: ${metrics.frontBiasPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                    )
                    Text(
                        text = "${stringResource(R.string.label_rear)}: ${metrics.rearBiasPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Dual Proportional Bar
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.desc_load_transfer),
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                )
            }
        }

        // 3. Kamm's Friction Circle & Cornering Grip Margin
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_kamm_circle),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val gripRiskColor = when {
                        metrics.gripMarginPercent > 50 -> AccentGreen
                        metrics.gripMarginPercent > 20 -> AccentOrange
                        else -> AccentRed
                    }
                    Text(
                        text = "${metrics.gripMarginPercent}% ${stringResource(R.string.label_margin)}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = gripRiskColor,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MetricMiniBox(
                        label = stringResource(R.string.label_roll_lean),
                        value = String.format(Locale.US, "%.1f°", metrics.leanAngleDeg),
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = stringResource(R.string.label_lateral_g),
                        value = String.format(Locale.US, "%.2f G", metrics.lateralGForce),
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = stringResource(R.string.label_total_g),
                        value = String.format(Locale.US, "%.2f G", metrics.totalGForce),
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.desc_kamm_circle),
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                )
            }
        }

        // 4. Kinetic Power & Drag Detection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_power_drag),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format(Locale.US, "%.0f W %s", metrics.currentBrakingPowerWatts, stringResource(R.string.label_dissipated)),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Involuntary Drag Box
                val hasDrag = metrics.wastedDragWatts > 5f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasDrag) AccentRed.copy(alpha = 0.15f) else DarkSurface)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (hasDrag) "[!]" else "[OK]",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasDrag) AccentRed else AccentGreen,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasDrag) {
                                stringResource(R.string.alert_brake_drag_detected, metrics.wastedDragWatts.toInt())
                            } else {
                                stringResource(R.string.label_zero_drag)
                            },
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasDrag) AccentRed else AccentGreen,
                            maxLines = 1,
                        )
                        Text(
                            text = if (hasDrag) stringResource(R.string.desc_drag_rubbing) else stringResource(R.string.desc_drag_clean),
                            fontSize = 10.5.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp,
                        )
                    }
                }
            }
        }

        // 4b. WorldTour PRO Descent & Trail-Braking Radar Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_worldtour_pro),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val desScore = metrics.descentEfficiencyScore
                    val desColor = when {
                        desScore >= 80 -> AccentGreen
                        desScore >= 60 -> AccentOrange
                        else -> AccentRed
                    }
                    Text(
                        text = "${stringResource(R.string.label_des_score)}: $desScore%",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = desColor,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val trailQualityName = when (metrics.trailBrakeQuality) {
                        TrailBrakeQuality.NONE -> stringResource(R.string.trail_quality_none)
                        TrailBrakeQuality.PERFECT -> stringResource(R.string.trail_quality_perfect)
                        TrailBrakeQuality.GOOD -> stringResource(R.string.trail_quality_good)
                        TrailBrakeQuality.EARLY_RELEASE -> stringResource(R.string.trail_quality_early)
                        TrailBrakeQuality.PANIC_LEAN -> stringResource(R.string.trail_quality_panic)
                    }
                    val trailColor = when (metrics.trailBrakeQuality) {
                        TrailBrakeQuality.PERFECT -> AccentGreen
                        TrailBrakeQuality.GOOD -> AccentCyan
                        TrailBrakeQuality.EARLY_RELEASE -> AccentOrange
                        TrailBrakeQuality.PANIC_LEAN -> AccentRed
                        TrailBrakeQuality.NONE -> TextSecondary
                    }

                    MetricMiniBox(
                        label = stringResource(R.string.label_trail_brake),
                        value = trailQualityName,
                        color = trailColor,
                        modifier = Modifier.weight(1.2f)
                    )
                    MetricMiniBox(
                        label = stringResource(R.string.type_pad_life),
                        value = "${metrics.padLifeRemainingPercent}%",
                        color = if (metrics.padLifeRemainingPercent > 20) AccentGreen else AccentRed,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                if (metrics.isPadGlazingAlert) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentRed.copy(alpha = 0.2f))
                            .border(1.dp, AccentRed, RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "[!]",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.alert_glazing_title),
                            color = AccentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // 4c. WorldTour PRO Corner Radar & Kamm Grip Limit Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.type_corner_radar),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val score = metrics.cornerScore
                    val scoreColor = when {
                        score >= 80f -> AccentGreen
                        score >= 60f -> AccentOrange
                        else -> TextSecondary
                    }
                    Text(
                        text = "${stringResource(R.string.label_corner_score)}: ${score.toInt()}%",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MetricMiniBox(
                        label = stringResource(R.string.label_apex_speed),
                        value = if (metrics.targetCornerSpeedKmh > 0) String.format(Locale.US, "%.1f km/h", metrics.targetCornerSpeedKmh) else "--",
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = stringResource(R.string.label_apex_retention),
                        value = if (metrics.apexSpeedRetentionPercent > 0) "${metrics.apexSpeedRetentionPercent.toInt()}%" else "--",
                        color = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        label = stringResource(R.string.label_peloton_pei),
                        value = "${metrics.pelotonSmoothnessIndex.toInt()}%",
                        color = Color(0xFFAB47BC),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (metrics.isCornerBrakeWarning) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentOrange.copy(alpha = 0.2f))
                            .border(1.dp, AccentOrange, RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "[!]",
                            color = AccentOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(
                                R.string.alert_corner_braking_detail,
                                metrics.targetBrakingDistanceMeters,
                                metrics.cornerEntrySpeedKmh,
                                metrics.targetCornerSpeedKmh
                            ),
                            color = AccentOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                if (metrics.isGripAlarmActive) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentRed.copy(alpha = 0.2f))
                            .border(1.dp, AccentRed, RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "[!]",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(
                                R.string.alert_grip_limit_detail,
                                metrics.gripMarginPercent,
                                metrics.leanAngleDeg
                            ),
                            color = AccentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
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
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_dual_thermal),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val brakeType = preferences.getBrakeType()
                    val criticalDisplay = tempUnit.fromCelsius(brakeType.criticalTempCelsius)
                    Text(
                        text = "${stringResource(R.string.label_limit)}: ${criticalDisplay.toInt()}${tempUnit.symbol}",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Front Rotor Box
                    val frontTempDisp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                    val frontColor = getTempStatusColor(metrics.frontTempStatus)
                    val frontStatusStr = when (metrics.frontTempStatus) {
                        TempStatus.NORMAL -> stringResource(R.string.label_zone_safe)
                        TempStatus.WARM -> stringResource(R.string.label_zone_warm)
                        TempStatus.HOT -> stringResource(R.string.label_zone_hot)
                        TempStatus.CRITICAL -> stringResource(R.string.label_zone_critical)
                    }
                    ThermalRotorBox(
                        title = stringResource(R.string.label_front_rotor),
                        tempDisplay = String.format(Locale.US, "%.0f%s", frontTempDisp, tempUnit.symbol),
                        status = frontStatusStr,
                        statusColor = frontColor,
                        coolingNote = stringResource(R.string.desc_cooling_front),
                        modifier = Modifier.weight(1f)
                    )

                    // Rear Rotor Box
                    val rearTempDisp = tempUnit.fromCelsius(metrics.rearTempCelsius)
                    val rearColor = getTempStatusColor(metrics.rearTempStatus)
                    val rearStatusStr = when (metrics.rearTempStatus) {
                        TempStatus.NORMAL -> stringResource(R.string.label_zone_safe)
                        TempStatus.WARM -> stringResource(R.string.label_zone_warm)
                        TempStatus.HOT -> stringResource(R.string.label_zone_hot)
                        TempStatus.CRITICAL -> stringResource(R.string.label_zone_critical)
                    }
                    ThermalRotorBox(
                        title = stringResource(R.string.label_rear_rotor),
                        tempDisplay = String.format(Locale.US, "%.0f%s", rearTempDisp, tempUnit.symbol),
                        status = rearStatusStr,
                        statusColor = rearColor,
                        coolingNote = stringResource(R.string.desc_cooling_rear),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Ride Statistics Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_max_decel),
                value = String.format(Locale.US, "%.2f %s", displayPeakDecel, unitLabel),
                subtitle = stringResource(R.string.label_peak_g_subtitle),
                color = AccentOrange,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_brake_events),
                value = "${metrics.totalBrakeEvents}",
                subtitle = stringResource(R.string.label_stops_subtitle),
                color = AccentGreen,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_brake_energy),
                value = String.format(Locale.US, "%.1f kJ", metrics.totalEnergyKj),
                subtitle = stringResource(R.string.label_absorbed_subtitle),
                color = AccentCyan,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_pad_wear),
                value = String.format(Locale.US, "%.1f pts", metrics.padWearScore),
                subtitle = stringResource(R.string.label_wear_subtitle),
                color = AccentRed,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetricCard(
                title = stringResource(R.string.type_metabolic_burn),
                value = String.format(Locale.US, "%.1f kJ", metrics.metabolicKjBurned),
                subtitle = String.format(Locale.US, "%.1f kcal", metrics.metabolicKjBurned / 4.184),
                color = AccentOrange,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = stringResource(R.string.type_peloton_smoothness),
                value = "${metrics.pelotonSmoothnessIndex}%",
                subtitle = stringResource(R.string.label_peloton_pei),
                color = Color(0xFFAB47BC),
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
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Section Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.screens_gallery_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.screens_gallery_desc),
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }
        }

        // Mockup 1: Brake Gauge Widget
        WidgetMockupCard(
            title = stringResource(R.string.widget_gauge_title),
            dataType = "graphical_brake",
            description = stringResource(R.string.widget_gauge_desc),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(R.string.type_current_decel), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = stringResource(R.string.status_armed), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "2.85", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = decelUnit.symbol, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${stringResource(R.string.label_peak_short)}: 4.10 ${decelUnit.symbol}",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
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
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(R.string.label_front_abbr)}: 70%",
                        fontSize = 11.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.label_rear_abbr)}: 30%",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB74D),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Mockup 2: Dual Thermal & Fade Monitor Widget
        WidgetMockupCard(
            title = stringResource(R.string.widget_thermal_title),
            dataType = "graphical_thermal",
            description = stringResource(R.string.widget_thermal_desc),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(R.string.type_graphical_thermal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentRed)
                    Text(text = "${stringResource(R.string.label_limit)}: 380°C", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface)
                            .padding(6.dp),
                    ) {
                        Text(text = stringResource(R.string.label_front_rotor), fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "118${tempUnit.symbol}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
                        Text(
                            text = "${stringResource(R.string.label_zone)}: ${stringResource(R.string.label_zone_warm)}",
                            fontSize = 10.5.sp,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface)
                            .padding(6.dp),
                    ) {
                        Text(text = stringResource(R.string.label_rear_rotor), fontSize = 11.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                        Text(text = "72${tempUnit.symbol}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AccentGreen)
                        Text(
                            text = "${stringResource(R.string.label_zone)}: ${stringResource(R.string.label_zone_safe)}",
                            fontSize = 10.5.sp,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Mockup 3: Master Cockpit HUD Widget
        WidgetMockupCard(
            title = stringResource(R.string.widget_cockpit_title),
            dataType = "graphical_cockpit",
            description = stringResource(R.string.widget_cockpit_desc),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1218))
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(R.string.type_graphical_cockpit), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                    Text(text = "85% ${stringResource(R.string.label_margin)}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "1.92 ${decelUnit.symbol}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text(text = "340 W ${stringResource(R.string.label_dissipated)}", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${stringResource(R.string.label_roll_lean)}: 24°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                        Text(
                            text = "${stringResource(R.string.label_front_abbr)}: 95°C | ${stringResource(R.string.label_rear_abbr)}: 68°C",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
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
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.screens_color_legend_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(8.dp))

                ColorLegendRow(Color(0xFFFFFFFF), stringResource(R.string.color_white), stringResource(R.string.color_white_desc))
                ColorLegendRow(Color(0xFFFFF176), stringResource(R.string.color_pale_yellow), stringResource(R.string.color_pale_yellow_desc))
                ColorLegendRow(Color(0xFFFFD600), stringResource(R.string.color_pure_yellow), stringResource(R.string.color_pure_yellow_desc))
                ColorLegendRow(Color(0xFFFF6D00), stringResource(R.string.color_pure_orange), stringResource(R.string.color_pure_orange_desc))
                ColorLegendRow(Color(0xFFD50000), stringResource(R.string.color_fiery_red), stringResource(R.string.color_fiery_red_desc))
                ColorLegendRow(Color(0xFFAB47BC), stringResource(R.string.color_violet), stringResource(R.string.color_violet_desc))
                ColorLegendRow(AccentGreen, stringResource(R.string.color_green), stringResource(R.string.color_green_desc))
                ColorLegendRow(AccentCyan, stringResource(R.string.color_cyan), stringResource(R.string.color_cyan_desc))
                ColorLegendRow(AccentOrange, stringResource(R.string.color_amber), stringResource(R.string.color_amber_desc))
                ColorLegendRow(AccentRed, stringResource(R.string.color_neon_red), stringResource(R.string.color_neon_red_desc))
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
                .size(13.dp)
                .clip(CircleShape)
                .background(color)
                .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$name: ",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = desc,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            lineHeight = 15.sp,
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
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = dataType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                        maxLines = 1,
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
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
    val padCompound by preferences.padCompoundFlow.collectAsState()
    val cumulativeEnergyKj by preferences.cumulativePadEnergyKjFlow.collectAsState()
    val glazingAlert by preferences.glazingAlertFlow.collectAsState()
    val descentEfficiencyAlert by preferences.descentEfficiencyAlertFlow.collectAsState()
    val cornerWarning by preferences.cornerWarningFlow.collectAsState()
    val gripLimitAlert by preferences.gripLimitAlertFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // 1. Regional Units System Preset
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.title_unit_system),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Text(
                    text = stringResource(R.string.desc_unit_system),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.unit_system_metric),
                                fontSize = 13.sp,
                                fontWeight = if (isMetric) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMetric) TextPrimary else TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.unit_system_metric_desc),
                                fontSize = 11.sp,
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
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.unit_system_imperial),
                                fontSize = 13.sp,
                                fontWeight = if (isImperial) FontWeight.Bold else FontWeight.Medium,
                                color = if (isImperial) TextPrimary else TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.unit_system_imperial_desc),
                                fontSize = 11.sp,
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
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.title_lever_calibration),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = stringResource(R.string.desc_lever_calibration),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Front Lever Selector
                Text(
                    text = stringResource(R.string.title_front_lever),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                .padding(vertical = 8.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(feel.titleRes),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rear Lever Selector
                Text(
                    text = stringResource(R.string.title_rear_lever),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                .padding(vertical = 8.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(feel.titleRes),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                textAlign = TextAlign.Center,
                            )
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
            Column(modifier = Modifier.padding(12.dp)) {
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
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.title_brake_type),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = stringResource(R.string.desc_brake_type),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                BrakeType.entries.forEach { type ->
                    val isSelected = brakeType == type
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentOrange.copy(alpha = 0.15f) else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) AccentOrange else Color(0xFF2C3440),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { preferences.setBrakeType(type) }
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(type.titleRes),
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                )
                                val limitDisplay = tempUnit.fromCelsius(type.criticalTempCelsius)
                                Text(
                                    text = "${stringResource(R.string.label_wear)}: ${type.wearFactor}x | ${stringResource(R.string.label_limit)}: ${limitDisplay.toInt()}${tempUnit.symbol}",
                                    fontSize = 11.sp,
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

        // 4b. Brake Pad Compound & Digital Service Odometer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.title_pad_compound),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Text(
                    text = stringResource(R.string.desc_pad_compound),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Compound Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    PadCompound.entries.forEach { compound ->
                        val isSelected = padCompound == compound
                        val compoundName = when (compound) {
                            PadCompound.RESIN -> stringResource(R.string.pad_compound_resin)
                            PadCompound.SEMI_METALLIC -> stringResource(R.string.pad_compound_semi_metallic)
                            PadCompound.METALLIC -> stringResource(R.string.pad_compound_metallic)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentOrange.copy(alpha = 0.2f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) AccentOrange else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setPadCompound(compound) }
                                .padding(vertical = 7.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = compoundName,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) AccentOrange else TextSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Odometer Progress & Readouts
                val ratedKj = padCompound.ratedEnergyKj
                val remainingPercent = ((1.0 - (cumulativeEnergyKj / ratedKj)).coerceIn(0.0, 1.0) * 100).toInt()
                val progressColor = when {
                    remainingPercent > 40 -> AccentGreen
                    remainingPercent > 15 -> AccentOrange
                    else -> AccentRed
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.title_pad_odometer),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "$remainingPercent%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = progressColor,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((remainingPercent / 100f).coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(progressColor)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${stringResource(R.string.label_cumulative_energy)}: ${String.format(Locale.US, "%.1f", cumulativeEnergyKj)} kJ",
                        fontSize = 11.sp,
                        color = TextSecondary,
                    )
                    Text(
                        text = "${stringResource(R.string.label_rated_capacity)}: ${ratedKj.toInt()} kJ",
                        fontSize = 11.sp,
                        color = TextSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reset Button
                Button(
                    onClick = { preferences.resetPadOdometer() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurface,
                        contentColor = AccentOrange
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF374151)),
                ) {
                    Text(
                        text = stringResource(R.string.btn_reset_pad_odometer),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // 5. Total Weight Stepper & Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                        )
                        Text(
                            text = stringResource(R.string.desc_total_weight),
                            fontSize = 11.5.sp,
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

                Spacer(modifier = Modifier.height(6.dp))

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
                }
            }
        }

        // 6. Surface Profile Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.title_surface),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )
                Text(
                    text = stringResource(R.string.desc_surface),
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                                .padding(vertical = 7.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(profile.titleRes),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AccentGreen else TextSecondary,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "μ=${profile.frictionMu}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) AccentGreen else TextSecondary.copy(alpha = 0.6f),
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Custom Unit Overrides (Stacked layout for perfect readability)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.title_custom_units),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )

                // Temperature Unit Selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.title_temperature_unit),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.desc_choose_temp_unit),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isCelsius = tempUnit == TemperatureUnit.CELSIUS
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCelsius) AccentGreen.copy(alpha = 0.2f) else DarkSurface)
                                .border(
                                    width = if (isCelsius) 1.5.dp else 1.dp,
                                    color = if (isCelsius) AccentGreen else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setTemperatureUnit(TemperatureUnit.CELSIUS) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "°C (${stringResource(R.string.unit_system_metric)})",
                                fontSize = 12.sp,
                                fontWeight = if (isCelsius) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCelsius) AccentGreen else TextSecondary,
                            )
                        }

                        val isFahrenheit = tempUnit == TemperatureUnit.FAHRENHEIT
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFahrenheit) AccentGreen.copy(alpha = 0.2f) else DarkSurface)
                                .border(
                                    width = if (isFahrenheit) 1.5.dp else 1.dp,
                                    color = if (isFahrenheit) AccentGreen else Color(0xFF2C3440),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { preferences.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "°F (${stringResource(R.string.unit_system_imperial)})",
                                fontSize = 12.sp,
                                fontWeight = if (isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFahrenheit) AccentGreen else TextSecondary,
                            )
                        }
                    }
                }

                // Deceleration Unit Selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.title_unit),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.desc_choose_decel_unit),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DecelUnit.entries.forEach { unit ->
                            val isSelected = decelUnit == unit
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else DarkSurface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) AccentCyan else Color(0xFF2C3440),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { preferences.setDecelUnit(unit) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = unit.symbol,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AccentCyan else TextSecondary,
                                )
                            }
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
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    title = stringResource(R.string.alert_glazing),
                    subtitle = stringResource(R.string.desc_glazing),
                    checked = glazingAlert,
                    onCheckedChange = { preferences.setGlazingAlertEnabled(it) },
                )

                SettingSwitchRow(
                    title = stringResource(R.string.alert_descent_efficiency),
                    subtitle = stringResource(R.string.desc_descent_efficiency_toggle),
                    checked = descentEfficiencyAlert,
                    onCheckedChange = { preferences.setDescentEfficiencyAlertEnabled(it) },
                )

                SettingSwitchRow(
                    title = stringResource(R.string.title_corner_warning),
                    subtitle = stringResource(R.string.desc_corner_warning),
                    checked = cornerWarning,
                    onCheckedChange = { preferences.setCornerWarningEnabled(it) },
                )

                SettingSwitchRow(
                    title = stringResource(R.string.title_grip_limit_alert),
                    subtitle = stringResource(R.string.desc_grip_limit_alert),
                    checked = gripLimitAlert,
                    onCheckedChange = { preferences.setGripLimitAlertEnabled(it) },
                )

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
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Quick Setup Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_guide_config),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.guide_step1),
                    fontSize = 12.5.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step2),
                    fontSize = 12.5.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step3),
                    fontSize = 12.5.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = stringResource(R.string.guide_step4),
                    fontSize = 12.5.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        // WorldTour Braking Science & Engineering Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_guide_science),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                )
                Spacer(modifier = Modifier.height(6.dp))

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic1_title),
                    explanation = stringResource(R.string.science_topic1_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic2_title),
                    explanation = stringResource(R.string.science_topic2_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic3_title),
                    explanation = stringResource(R.string.science_topic3_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic4_title),
                    explanation = stringResource(R.string.science_topic4_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic5_title),
                    explanation = stringResource(R.string.science_topic5_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic6_title),
                    explanation = stringResource(R.string.science_topic6_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic7_title),
                    explanation = stringResource(R.string.science_topic7_desc),
                )

                ScienceTopicItem(
                    title = stringResource(R.string.science_topic8_title),
                    explanation = stringResource(R.string.science_topic8_desc),
                )
            }
        }

        // Data Fields Catalog Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.title_fields_catalog),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                )
                Spacer(modifier = Modifier.height(6.dp))

                DataFieldItem(stringResource(R.string.type_current_decel), stringResource(R.string.desc_current_decel))
                DataFieldItem(stringResource(R.string.type_braking_power), stringResource(R.string.desc_braking_power))
                DataFieldItem(stringResource(R.string.type_max_decel), stringResource(R.string.desc_max_decel))
                DataFieldItem(stringResource(R.string.type_brake_events), stringResource(R.string.desc_brake_events))
                DataFieldItem(stringResource(R.string.type_brake_energy), stringResource(R.string.desc_brake_energy))
                DataFieldItem(stringResource(R.string.type_pad_wear), stringResource(R.string.desc_pad_wear))
                DataFieldItem(stringResource(R.string.type_pad_life), stringResource(R.string.desc_pad_life))
                DataFieldItem(stringResource(R.string.type_descent_efficiency), stringResource(R.string.desc_descent_efficiency))
                DataFieldItem(stringResource(R.string.type_wasted_watts), stringResource(R.string.desc_wasted_watts))
                DataFieldItem(stringResource(R.string.type_grip_margin), stringResource(R.string.desc_grip_margin))
                DataFieldItem(stringResource(R.string.type_brake_temperature), stringResource(R.string.desc_brake_temperature))
                DataFieldItem(stringResource(R.string.type_corner_radar), stringResource(R.string.desc_corner_radar))
                DataFieldItem(stringResource(R.string.type_metabolic_burn), stringResource(R.string.desc_metabolic_burn))
                DataFieldItem(stringResource(R.string.type_peloton_smoothness), stringResource(R.string.desc_peloton_smoothness))
                DataFieldItem(stringResource(R.string.type_graphical_brake), stringResource(R.string.desc_graphical_brake))
                DataFieldItem(stringResource(R.string.type_graphical_thermal), stringResource(R.string.desc_graphical_thermal))
                DataFieldItem(stringResource(R.string.type_graphical_cockpit), stringResource(R.string.desc_graphical_cockpit))
            }
        }
    }
}

@Composable
fun ScienceTopicItem(title: String, explanation: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = explanation, fontSize = 11.5.sp, color = TextSecondary, lineHeight = 15.sp)
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
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(text = status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, maxLines = 1)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = tempDisplay, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
        Text(text = coolingNote, fontSize = 10.5.sp, color = TextSecondary.copy(alpha = 0.7f), maxLines = 1)
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
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, fontSize = 10.5.sp, color = TextSecondary, maxLines = 1)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
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
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = TextSecondary.copy(alpha = 0.75f),
                maxLines = 1,
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
            Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.width(6.dp))
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
fun DataFieldItem(name: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = "• $name", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(start = 8.dp))
    }
}
