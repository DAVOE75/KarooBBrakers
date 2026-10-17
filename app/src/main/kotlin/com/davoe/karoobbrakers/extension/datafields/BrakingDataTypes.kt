package com.davoe.karoobbrakers.extension.datafields

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.davoe.karoobbrakers.R
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.extension.render.BrakeFieldRenderer
import com.davoe.karoobbrakers.extension.render.BrakeFieldRenderer.TelemetryTileData
import com.davoe.karoobbrakers.extension.ui.BrakeCockpitGlanceWidget
import com.davoe.karoobbrakers.extension.ui.BrakeGaugeGlanceWidget
import com.davoe.karoobbrakers.extension.ui.BrakeThermalGlanceWidget
import com.davoe.karoobbrakers.model.TempStatus
import com.davoe.karoobbrakers.sensor.BrakeSensorFusion
import java.util.Locale
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CurrentBrakeDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val value = preferences.formatDecelValue(metrics.currentDecelMs2)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to value),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_current_decel)
        val unit = preferences.getDecelSymbol()
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.1f", preferences.formatDecelValue(metrics.currentDecelMs2))
            val valColor = if (metrics.isBraking) {
                if (metrics.currentDecelMs2 > 3.0) 0xFFFF1744.toInt() else 0xFFFFFFFF.toInt()
            } else 0xFF78909C.toInt()
            val accentColor = if (metrics.isBraking) 0xFFFF1744.toInt() else 0xFF00E5FF.toInt()
            TelemetryTileData(
                label = label,
                value = value,
                unit = unit,
                valueColor = valColor,
                accentBarColor = accentColor,
            )
        }
    }

    companion object {
        const val TYPE_ID = "current_decel"
    }
}

class BrakingPowerDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.currentBrakingPowerWatts),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_braking_power)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.0f", metrics.currentBrakingPowerWatts)
            val valColor = if (metrics.currentBrakingPowerWatts > 0.0) 0xFFFFFFFF.toInt() else 0xFF78909C.toInt()
            val accentColor = if (metrics.currentBrakingPowerWatts > 400.0) 0xFFFF3D00.toInt() else 0xFFFF9100.toInt()
            TelemetryTileData(
                label = label,
                value = value,
                unit = "W",
                valueColor = valColor,
                accentBarColor = accentColor,
            )
        }
    }

    companion object {
        const val TYPE_ID = "braking_power"
    }
}

class MaxBrakeDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val value = preferences.formatDecelValue(metrics.peakDecelMs2)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to value),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_max_decel)
        val unit = preferences.getDecelSymbol()
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.1f", preferences.formatDecelValue(metrics.peakDecelMs2))
            TelemetryTileData(
                label = label,
                value = value,
                unit = unit,
                valueColor = 0xFFFFD600.toInt(),
                accentBarColor = 0xFFFFD600.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "max_decel"
    }
}

class BrakeEventsCountDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.totalBrakeEvents.toDouble()),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_brake_events)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            TelemetryTileData(
                label = label,
                value = "${metrics.totalBrakeEvents}",
                unit = "",
                valueColor = 0xFFFFFFFF.toInt(),
                accentBarColor = 0xFF00E5FF.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "brake_events"
    }
}

class BrakeEnergyDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.totalEnergyKj),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_brake_energy)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.1f", metrics.totalEnergyKj)
            TelemetryTileData(
                label = label,
                value = value,
                unit = "kJ",
                valueColor = 0xFFFF9100.toInt(),
                accentBarColor = 0xFFFF9100.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "brake_energy"
    }
}

class PadWearDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.padWearScore),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_pad_wear)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.1f", metrics.padWearScore)
            TelemetryTileData(
                label = label,
                value = value,
                unit = "pts",
                valueColor = 0xFFFF5252.toInt(),
                accentBarColor = 0xFFFF5252.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "pad_wear"
    }
}

class PadLifeDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.padLifeRemainingPercent.toDouble()),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_pad_life)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val pct = metrics.padLifeRemainingPercent
            val color = when {
                pct > 40 -> 0xFF00E676.toInt()
                pct > 20 -> 0xFFFFD600.toInt()
                else -> 0xFFFF1744.toInt()
            }
            TelemetryTileData(
                label = label,
                value = "$pct",
                unit = "%",
                valueColor = color,
                accentBarColor = color,
            )
        }
    }

    companion object {
        const val TYPE_ID = "pad_life"
    }
}

class DescentEfficiencyDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.descentEfficiencyScore.toDouble()),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_descent_efficiency)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val score = metrics.descentEfficiencyScore
            val color = when {
                score >= 80 -> 0xFF00E676.toInt()
                score >= 60 -> 0xFFFFD600.toInt()
                else -> 0xFFFF5252.toInt()
            }
            TelemetryTileData(
                label = label,
                value = "$score",
                unit = "%",
                valueColor = color,
                accentBarColor = color,
            )
        }
    }

    companion object {
        const val TYPE_ID = "descent_efficiency"
    }
}

class WastedWattsDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.wastedDragWatts),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_wasted_watts)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val value = String.format(Locale.US, "%.0f", metrics.wastedDragWatts)
            TelemetryTileData(
                label = label,
                value = value,
                unit = "W",
                valueColor = 0xFFFF5252.toInt(),
                accentBarColor = 0xFFFF5252.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "wasted_watts"
    }
}

class GripMarginDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.gripMarginPercent.toDouble()),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_grip_margin)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val margin = metrics.gripMarginPercent
            val color = when {
                margin >= 40 -> 0xFF00E676.toInt()
                margin >= 20 -> 0xFFFFD600.toInt()
                else -> 0xFFFF1744.toInt()
            }
            TelemetryTileData(
                label = label,
                value = "$margin",
                unit = "%",
                valueColor = color,
                accentBarColor = color,
            )
        }
    }

    companion object {
        const val TYPE_ID = "grip_margin"
    }
}

class BrakeTemperatureDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val tempVal = preferences.getTemperatureUnit().fromCelsius(metrics.estimatedTempCelsius)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to tempVal),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_brake_temperature)
        val tempUnit = preferences.getTemperatureUnit()
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val tempVal = tempUnit.fromCelsius(metrics.estimatedTempCelsius)
            val color = when (metrics.tempStatus) {
                TempStatus.NORMAL -> 0xFF00E676.toInt()
                TempStatus.WARM -> 0xFF00E5FF.toInt()
                TempStatus.HOT -> 0xFFFF9100.toInt()
                TempStatus.CRITICAL -> 0xFFFF1744.toInt()
            }
            TelemetryTileData(
                label = label,
                value = String.format(Locale.US, "%.0f", tempVal),
                unit = tempUnit.symbol,
                valueColor = color,
                accentBarColor = color,
            )
        }
    }

    companion object {
        const val TYPE_ID = "brake_temperature"
    }
}

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class BrakeGraphicalDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {
    private val glance = GlanceRemoteViews()

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val value = preferences.formatDecelValue(metrics.currentDecelMs2)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to value),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        var viewJob: Job? = null
        val scope = CoroutineScope(Dispatchers.Main)
        var lastRenderTimeMs = 0L

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val now = System.currentTimeMillis()
                // Throttle View updates to ~8 Hz (every 120ms) to conserve CPU/Binder
                if (now - lastRenderTimeMs >= 120L || metrics.isBraking != (lastRenderTimeMs == 0L)) {
                    lastRenderTimeMs = now

                    val unitLabel = preferences.getDecelSymbol()
                    val displayVal = preferences.formatDecelValue(metrics.currentDecelMs2)

                    val statusText = when {
                        metrics.isPedalingActive -> context.getString(R.string.status_pedal)
                        metrics.isBraking -> context.getString(R.string.status_brake)
                        metrics.isGroupRidingFeathering -> context.getString(R.string.status_peloton)
                        else -> context.getString(R.string.status_armed)
                    }
                    val peakLabel = context.getString(R.string.label_peak_short)
                    val frontLabel = context.getString(R.string.label_front_abbr)
                    val rearLabel = context.getString(R.string.label_rear_abbr)

                    val decelText = if (metrics.isBraking) String.format(Locale.US, "%.1f", displayVal) else "0.0"
                    val decelColor = if (metrics.isBraking) 0xFFFFFFFF.toInt() else 0xFF78909C.toInt()
                    val decelBitmap = BrakeFieldRenderer.renderNumberBitmap(context, decelText, decelColor, pixelHeight = 56)

                    val result = glance.compose(context, DpSize.Unspecified) {
                        BrakeGaugeGlanceWidget(
                            value = displayVal,
                            unit = unitLabel,
                            intensityPercent = metrics.brakingIntensityPercent,
                            isBraking = metrics.isBraking,
                            isPedaling = metrics.isPedalingActive,
                            peakG = metrics.peakGForce,
                            isGroupFeathering = metrics.isGroupRidingFeathering,
                            frontBias = metrics.frontBiasPercent,
                            rearBias = metrics.rearBiasPercent,
                            statusText = statusText,
                            peakLabel = peakLabel,
                            frontLabel = frontLabel,
                            rearLabel = rearLabel,
                            decelBitmap = decelBitmap,
                        )
                    }
                    emitter.updateView(result.remoteViews)
                }
            }
        }

        emitter.setCancellable {
            viewJob?.cancel()
        }
    }

    companion object {
        const val TYPE_ID = "graphical_brake"
    }
}

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class BrakeThermalGraphicalDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {
    private val glance = GlanceRemoteViews()

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val tempVal = preferences.getTemperatureUnit().fromCelsius(metrics.estimatedTempCelsius)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to tempVal),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        var viewJob: Job? = null
        val scope = CoroutineScope(Dispatchers.Main)
        var lastRenderTimeMs = 0L

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val now = System.currentTimeMillis()
                if (now - lastRenderTimeMs >= 150L) {
                    lastRenderTimeMs = now

                    val tempUnit = preferences.getTemperatureUnit()
                    val currentTemp = tempUnit.fromCelsius(metrics.estimatedTempCelsius)
                    val peakTemp = tempUnit.fromCelsius(metrics.peakTempCelsius)
                    val frontTemp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                    val rearTemp = tempUnit.fromCelsius(metrics.rearTempCelsius)
                    val brakeType = preferences.getBrakeType()
                    val criticalTemp = tempUnit.fromCelsius(brakeType.criticalTempCelsius)

                    val tempStatusText = when (metrics.tempStatus) {
                        TempStatus.NORMAL -> context.getString(R.string.label_zone_safe)
                        TempStatus.WARM -> context.getString(R.string.label_zone_warm)
                        TempStatus.HOT -> context.getString(R.string.label_zone_hot)
                        TempStatus.CRITICAL -> context.getString(R.string.label_zone_critical)
                    }
                    val heatLabel = context.getString(R.string.label_heat)
                    val maxLabel = context.getString(R.string.label_max_abbr)
                    val frontLabel = context.getString(R.string.label_front).uppercase(Locale.getDefault())
                    val rearLabel = context.getString(R.string.label_rear).uppercase(Locale.getDefault())
                    val safetyLimitLabel = context.getString(R.string.label_safety_limit)
                    val localizedBrakeTypeName = context.getString(brakeType.titleRes)

                    fun colorForStatusInt(status: TempStatus): Int = when (status) {
                        TempStatus.NORMAL -> 0xFF00E676.toInt()
                        TempStatus.WARM -> 0xFF00E5FF.toInt()
                        TempStatus.HOT -> 0xFFFF9100.toInt()
                        TempStatus.CRITICAL -> 0xFFFF1744.toInt()
                    }

                    val frontBitmap = BrakeFieldRenderer.renderNumberBitmap(
                        context,
                        String.format(Locale.US, "%.0f%s", frontTemp, tempUnit.symbol),
                        colorForStatusInt(metrics.frontTempStatus),
                        pixelHeight = 44,
                    )
                    val rearBitmap = BrakeFieldRenderer.renderNumberBitmap(
                        context,
                        String.format(Locale.US, "%.0f%s", rearTemp, tempUnit.symbol),
                        colorForStatusInt(metrics.rearTempStatus),
                        pixelHeight = 44,
                    )

                    val result = glance.compose(context, DpSize.Unspecified) {
                        BrakeThermalGlanceWidget(
                            currentTemp = currentTemp,
                            peakTemp = peakTemp,
                            criticalTemp = criticalTemp,
                            unitSymbol = tempUnit.symbol,
                            tempStatus = metrics.tempStatus,
                            brakeTypeName = localizedBrakeTypeName,
                            padWearScore = metrics.padWearScore,
                            frontTemp = frontTemp,
                            rearTemp = rearTemp,
                            frontTempStatus = metrics.frontTempStatus,
                            rearTempStatus = metrics.rearTempStatus,
                            statusText = tempStatusText,
                            heatLabel = heatLabel,
                            maxLabel = maxLabel,
                            frontLabel = frontLabel,
                            rearLabel = rearLabel,
                            safetyLimitLabel = safetyLimitLabel,
                            frontTempBitmap = frontBitmap,
                            rearTempBitmap = rearBitmap,
                        )
                    }
                    emitter.updateView(result.remoteViews)
                }
            }
        }

        emitter.setCancellable {
            viewJob?.cancel()
        }
    }

    companion object {
        const val TYPE_ID = "graphical_thermal"
    }
}

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class BrakeCockpitGraphicalDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences,
) : DataTypeImpl(extension, TYPE_ID) {
    private val glance = GlanceRemoteViews()

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val decel = preferences.formatDecelValue(metrics.currentDecelMs2)
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to decel),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        var viewJob: Job? = null
        val scope = CoroutineScope(Dispatchers.Main)
        var lastRenderTimeMs = 0L

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val now = System.currentTimeMillis()
                if (now - lastRenderTimeMs >= 120L || metrics.isBraking != (lastRenderTimeMs == 0L)) {
                    lastRenderTimeMs = now

                    val decelUnit = preferences.getDecelSymbol()
                    val displayDecel = preferences.formatDecelValue(metrics.currentDecelMs2)
                    val tempUnit = preferences.getTemperatureUnit()
                    val currentTemp = tempUnit.fromCelsius(metrics.estimatedTempCelsius)

                    val frontTemp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                    val rearTemp = tempUnit.fromCelsius(metrics.rearTempCelsius)

                    val statusText = when {
                        metrics.isTrailBraking -> context.getString(R.string.status_trail)
                        metrics.isPedalingActive -> context.getString(R.string.status_pedal)
                        metrics.isBraking -> context.getString(R.string.status_brake)
                        metrics.isGroupRidingFeathering -> context.getString(R.string.status_peloton)
                        else -> context.getString(R.string.status_armed)
                    }
                    val hudLabel = context.getString(R.string.label_hud)
                    val peakLabel = context.getString(R.string.label_peak_short)
                    val powerLabel = context.getString(R.string.label_power_abbr)
                    val frontLabel = context.getString(R.string.label_front_abbr)
                    val rearLabel = context.getString(R.string.label_rear_abbr)

                    val decelText = if (metrics.isBraking) String.format(Locale.US, "%.1f", displayDecel) else "0.0"
                    val decelColor = if (metrics.isBraking) 0xFFFFFFFF.toInt() else 0xFF78909C.toInt()
                    val decelBitmap = BrakeFieldRenderer.renderNumberBitmap(context, decelText, decelColor, pixelHeight = 52)

                    val result = glance.compose(context, DpSize.Unspecified) {
                        BrakeCockpitGlanceWidget(
                            decelValue = displayDecel,
                            decelUnit = decelUnit,
                            powerWatts = metrics.currentBrakingPowerWatts,
                            temperature = currentTemp,
                            tempUnit = tempUnit.symbol,
                            tempStatus = metrics.tempStatus,
                            intensityPercent = metrics.brakingIntensityPercent,
                            isBraking = metrics.isBraking,
                            isPedaling = metrics.isPedalingActive,
                            peakG = metrics.peakGForce,
                            eventsCount = metrics.totalBrakeEvents,
                            isGroupFeathering = metrics.isGroupRidingFeathering,
                            frontBias = metrics.frontBiasPercent,
                            rearBias = metrics.rearBiasPercent,
                            frontTemp = frontTemp,
                            rearTemp = rearTemp,
                            frontTempStatus = metrics.frontTempStatus,
                            rearTempStatus = metrics.rearTempStatus,
                            statusText = statusText,
                            hudLabel = hudLabel,
                            peakLabel = peakLabel,
                            powerLabel = powerLabel,
                            frontLabel = frontLabel,
                            rearLabel = rearLabel,
                            descentEfficiencyScore = metrics.descentEfficiencyScore,
                            isDescending = metrics.isDescending,
                            isTrailBraking = metrics.isTrailBraking,
                            padLifeRemainingPercent = metrics.padLifeRemainingPercent,
                            decelBitmap = decelBitmap,
                        )
                    }
                    emitter.updateView(result.remoteViews)
                }
            }
        }

        emitter.setCancellable {
            viewJob?.cancel()
        }
    }

    companion object {
        const val TYPE_ID = "graphical_cockpit"
    }
}

class CornerRadarDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                val displayVal = if (metrics.apexSpeedKmh > 0.0) metrics.apexSpeedKmh else metrics.cornerEntrySpeedKmh
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to displayVal),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_corner_radar)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val speed = if (metrics.apexSpeedKmh > 0.0) metrics.apexSpeedKmh else metrics.cornerEntrySpeedKmh
            TelemetryTileData(
                label = label,
                value = String.format(Locale.US, "%.1f", speed),
                unit = "km/h",
                valueColor = 0xFF00E5FF.toInt(),
                accentBarColor = 0xFF00E5FF.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "corner_radar"
    }
}

class MetabolicBurnDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.metabolicKjBurned),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_metabolic_burn)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            TelemetryTileData(
                label = label,
                value = String.format(Locale.US, "%.1f", metrics.metabolicKjBurned),
                unit = "kJ",
                valueColor = 0xFFFF6D00.toInt(),
                accentBarColor = 0xFFFF6D00.toInt(),
            )
        }
    }

    companion object {
        const val TYPE_ID = "metabolic_burn"
    }
}

class PelotonSmoothnessDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
    private val preferences: BrakePreferences? = null,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            sensorFusion.metrics.collect { metrics ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to metrics.pelotonSmoothnessIndex.toDouble()),
                        )
                    )
                )
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val label = context.getString(R.string.type_peloton_smoothness)
        BrakeFieldRenderer.startTelemetryView(context, config, emitter, sensorFusion) { metrics ->
            val smoothness = metrics.pelotonSmoothnessIndex
            val color = when {
                smoothness >= 75 -> 0xFF00E676.toInt()
                smoothness >= 50 -> 0xFFFFD600.toInt()
                else -> 0xFFFF5252.toInt()
            }
            TelemetryTileData(
                label = label,
                value = "$smoothness",
                unit = "/100",
                valueColor = color,
                accentBarColor = color,
            )
        }
    }

    companion object {
        const val TYPE_ID = "peloton_smoothness"
    }
}

