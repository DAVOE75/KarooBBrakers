package com.davoe.karoobbrakers.extension.datafields

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.extension.ui.BrakeCockpitGlanceWidget
import com.davoe.karoobbrakers.extension.ui.BrakeGaugeGlanceWidget
import com.davoe.karoobbrakers.extension.ui.BrakeThermalGlanceWidget
import com.davoe.karoobbrakers.sensor.BrakeSensorFusion
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

    companion object {
        const val TYPE_ID = "current_decel"
    }
}

class BrakingPowerDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
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

    companion object {
        const val TYPE_ID = "max_decel"
    }
}

class BrakeEventsCountDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
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

    companion object {
        const val TYPE_ID = "brake_events"
    }
}

class BrakeEnergyDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
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

    companion object {
        const val TYPE_ID = "brake_energy"
    }
}

class PadWearDataType(
    extension: String,
    private val sensorFusion: BrakeSensorFusion,
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

    companion object {
        const val TYPE_ID = "pad_wear"
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

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val unitLabel = preferences.getDecelSymbol()
                val displayVal = preferences.formatDecelValue(metrics.currentDecelMs2)

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
                    )
                }
                emitter.updateView(result.remoteViews)
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

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val tempUnit = preferences.getTemperatureUnit()
                val currentTemp = tempUnit.fromCelsius(metrics.estimatedTempCelsius)
                val peakTemp = tempUnit.fromCelsius(metrics.peakTempCelsius)
                val frontTemp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                val rearTemp = tempUnit.fromCelsius(metrics.rearTempCelsius)
                val brakeType = preferences.getBrakeType()
                val criticalTemp = tempUnit.fromCelsius(brakeType.criticalTempCelsius)

                val result = glance.compose(context, DpSize.Unspecified) {
                    BrakeThermalGlanceWidget(
                        currentTemp = currentTemp,
                        peakTemp = peakTemp,
                        criticalTemp = criticalTemp,
                        unitSymbol = tempUnit.symbol,
                        tempStatus = metrics.tempStatus,
                        brakeTypeName = brakeType.name,
                        padWearScore = metrics.padWearScore,
                        frontTemp = frontTemp,
                        rearTemp = rearTemp,
                        frontTempStatus = metrics.frontTempStatus,
                        rearTempStatus = metrics.rearTempStatus,
                    )
                }
                emitter.updateView(result.remoteViews)
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

        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        viewJob = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val decelUnit = preferences.getDecelSymbol()
                val displayDecel = preferences.formatDecelValue(metrics.currentDecelMs2)
                val tempUnit = preferences.getTemperatureUnit()
                val currentTemp = tempUnit.fromCelsius(metrics.estimatedTempCelsius)

                val frontTemp = tempUnit.fromCelsius(metrics.frontTempCelsius)
                val rearTemp = tempUnit.fromCelsius(metrics.rearTempCelsius)

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
                    )
                }
                emitter.updateView(result.remoteViews)
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
