package com.davoe.karoobbrakers.extension

import android.content.Intent
import android.os.IBinder
import com.davoe.karoobbrakers.R
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.extension.datafields.BrakeCockpitGraphicalDataType
import com.davoe.karoobbrakers.extension.datafields.BrakeEnergyDataType
import com.davoe.karoobbrakers.extension.datafields.BrakeEventsCountDataType
import com.davoe.karoobbrakers.extension.datafields.BrakeGraphicalDataType
import com.davoe.karoobbrakers.extension.datafields.BrakeTemperatureDataType
import com.davoe.karoobbrakers.extension.datafields.BrakeThermalGraphicalDataType
import com.davoe.karoobbrakers.extension.datafields.BrakingPowerDataType
import com.davoe.karoobbrakers.extension.datafields.CurrentBrakeDataType
import com.davoe.karoobbrakers.extension.datafields.MaxBrakeDataType
import com.davoe.karoobbrakers.extension.datafields.PadWearDataType
import com.davoe.karoobbrakers.sensor.BrakeSensorFusion
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DeveloperField
import io.hammerhead.karooext.models.FieldValue
import io.hammerhead.karooext.models.FitEffect
import io.hammerhead.karooext.models.InRideAlert
import io.hammerhead.karooext.models.PlayBeepPattern
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.WriteToRecordMesg
import io.hammerhead.karooext.models.WriteToSessionMesg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.davoe.karoobbrakers.extension.datafields.DescentEfficiencyDataType
import com.davoe.karoobbrakers.extension.datafields.GripMarginDataType
import com.davoe.karoobbrakers.extension.datafields.PadLifeDataType
import com.davoe.karoobbrakers.extension.datafields.WastedWattsDataType
import com.davoe.karoobbrakers.extension.datafields.CornerRadarDataType
import com.davoe.karoobbrakers.extension.datafields.MetabolicBurnDataType
import com.davoe.karoobbrakers.extension.datafields.PelotonSmoothnessDataType
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class KarooBBrakersExtension : KarooExtension("karoobbrakers", "1.2.1") {

    private val karooSystem by lazy { KarooSystemService(this) }
    private val preferences by lazy { BrakePreferences(this) }
    private val sensorFusion by lazy { BrakeSensorFusion(this, preferences) }

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var streamJobs = mutableListOf<Job>()
    private var fitEmitter: Emitter<FitEffect>? = null
    private var isRecording = false

    private var lastHardAlertTimeMs = 0L
    private var lastThermalAlertTimeMs = 0L
    private var lastGlazingAlertTimeMs = 0L
    private var lastPadWearAlertTimeMs = 0L
    private var lastGripAlertTimeMs = 0L
    private var lastCornerAlertTimeMs = 0L

    // FIT Developer Data Fields Definitions
    private val devFieldDecel = DeveloperField(
        fieldDefinitionNumber = 0,
        fitBaseTypeId = 136, // Float32
        fieldName = "BrakingDeceleration",
        units = "m/s2",
    )
    private val devFieldPower = DeveloperField(
        fieldDefinitionNumber = 1,
        fitBaseTypeId = 132, // UInt16
        fieldName = "BrakingPower",
        units = "Watts",
    )
    private val devFieldIsBraking = DeveloperField(
        fieldDefinitionNumber = 2,
        fitBaseTypeId = 2, // UInt8
        fieldName = "IsBraking",
        units = "flag",
    )
    private val devFieldTotalEvents = DeveloperField(
        fieldDefinitionNumber = 3,
        fitBaseTypeId = 132, // UInt16
        fieldName = "TotalBrakeEvents",
        units = "count",
    )
    private val devFieldMaxDecel = DeveloperField(
        fieldDefinitionNumber = 4,
        fitBaseTypeId = 136, // Float32
        fieldName = "MaxBrakeDecel",
        units = "m/s2",
    )
    private val devFieldTotalEnergy = DeveloperField(
        fieldDefinitionNumber = 5,
        fitBaseTypeId = 136, // Float32
        fieldName = "TotalBrakeEnergy",
        units = "kJ",
    )
    private val devFieldPadWear = DeveloperField(
        fieldDefinitionNumber = 6,
        fitBaseTypeId = 136, // Float32
        fieldName = "PadWearScore",
        units = "score",
    )
    private val devFieldTemp = DeveloperField(
        fieldDefinitionNumber = 7,
        fitBaseTypeId = 136, // Float32
        fieldName = "BrakeTemperature",
        units = "deg_c",
    )
    private val devFieldDES = DeveloperField(
        fieldDefinitionNumber = 8,
        fitBaseTypeId = 2, // UInt8
        fieldName = "DescentEfficiency",
        units = "percent",
    )
    private val devFieldGripMargin = DeveloperField(
        fieldDefinitionNumber = 9,
        fitBaseTypeId = 2, // UInt8
        fieldName = "GripMargin",
        units = "percent",
    )
    private val devFieldMetabolic = DeveloperField(
        fieldDefinitionNumber = 10,
        fitBaseTypeId = 132, // UInt16
        fieldName = "MetabolicBurn",
        units = "kJ",
    )
    private val devFieldPelotonSmoothness = DeveloperField(
        fieldDefinitionNumber = 11,
        fitBaseTypeId = 2, // UInt8
        fieldName = "PelotonSmoothness",
        units = "percent",
    )
    private val devFieldApexRetention = DeveloperField(
        fieldDefinitionNumber = 12,
        fitBaseTypeId = 2, // UInt8
        fieldName = "ApexRetention",
        units = "percent",
    )

    override val types: List<DataTypeImpl> by lazy {
        listOf(
            CurrentBrakeDataType(extension, sensorFusion, preferences),
            BrakingPowerDataType(extension, sensorFusion),
            MaxBrakeDataType(extension, sensorFusion, preferences),
            BrakeEventsCountDataType(extension, sensorFusion),
            BrakeEnergyDataType(extension, sensorFusion),
            PadWearDataType(extension, sensorFusion),
            BrakeTemperatureDataType(extension, sensorFusion, preferences),
            DescentEfficiencyDataType(extension, sensorFusion),
            PadLifeDataType(extension, sensorFusion),
            WastedWattsDataType(extension, sensorFusion),
            GripMarginDataType(extension, sensorFusion),
            CornerRadarDataType(extension, sensorFusion),
            MetabolicBurnDataType(extension, sensorFusion),
            PelotonSmoothnessDataType(extension, sensorFusion),
            BrakeGraphicalDataType(extension, sensorFusion, preferences),
            BrakeThermalGraphicalDataType(extension, sensorFusion, preferences),
            BrakeCockpitGraphicalDataType(extension, sensorFusion, preferences),
        )
    }

    override fun onCreate() {
        super.onCreate()
        sensorFusion.start()

        // Configure Alert Callbacks
        sensorFusion.onHardBrakeDetected = { peakDecel, gForce ->
            val now = System.currentTimeMillis()
            if (now - lastHardAlertTimeMs > 4000L) {
                lastHardAlertTimeMs = now
                triggerHardBrakeAlarm(peakDecel, gForce)
            }
        }

        sensorFusion.onThermalWarningDetected = { brakeTypeName, tempCelsius ->
            val now = System.currentTimeMillis()
            if (now - lastThermalAlertTimeMs > 15000L) {
                lastThermalAlertTimeMs = now
                triggerThermalWarning(brakeTypeName, tempCelsius)
            }
        }

        sensorFusion.onGlazingWarningDetected = { tempCelsius, durationSec ->
            val now = System.currentTimeMillis()
            if (now - lastGlazingAlertTimeMs > 30000L) {
                lastGlazingAlertTimeMs = now
                triggerGlazingWarning(tempCelsius, durationSec)
            }
        }

        sensorFusion.onPadWearWarningDetected = { remainingPercent ->
            val now = System.currentTimeMillis()
            if (now - lastPadWearAlertTimeMs > 60000L) {
                lastPadWearAlertTimeMs = now
                triggerPadWearWarning(remainingPercent)
            }
        }

        sensorFusion.onGripLimitWarningDetected = { marginPct, rollDeg ->
            val now = System.currentTimeMillis()
            if (now - lastGripAlertTimeMs > 6000L) {
                lastGripAlertTimeMs = now
                triggerGripLimitWarning(marginPct, rollDeg)
            }
        }

        sensorFusion.onCornerBrakeWarningDetected = { distM, curKmh, tgtKmh ->
            val now = System.currentTimeMillis()
            if (now - lastCornerAlertTimeMs > 10000L) {
                lastCornerAlertTimeMs = now
                triggerCornerBrakeWarning(distM, curKmh, tgtKmh)
            }
        }

        // Connect Karoo System Service
        karooSystem.connect { connected ->
            if (connected) {
                observeKarooTelemetry()
            }
        }
    }

    private fun observeKarooTelemetry() {
        // 1. Observe Ride State
        streamJobs.add(
            serviceScope.launch {
                karooSystem.consumerFlow<RideState>().collect { state ->
                    when (state) {
                        is RideState.Recording -> {
                            isRecording = true
                        }
                        is RideState.Paused -> {
                            isRecording = false
                        }
                        is RideState.Idle -> {
                            if (isRecording) {
                                writeSessionFitSummary()
                            }
                            isRecording = false
                        }
                    }
                }
            }
        )

        // 2. Observe Speed (m/s)
        streamJobs.add(
            serviceScope.launch {
                karooSystem.streamDataFlow(io.hammerhead.karooext.models.DataType.Type.SPEED).collect { state ->
                    if (state is StreamState.Streaming) {
                        state.dataPoint.singleValue?.let { speedMs ->
                            sensorFusion.onSpeedUpdate(speedMs)
                        }
                    }
                }
            }
        )

        // 3. Observe Cadence (RPM)
        streamJobs.add(
            serviceScope.launch {
                karooSystem.streamDataFlow(io.hammerhead.karooext.models.DataType.Type.CADENCE).collect { state ->
                    if (state is StreamState.Streaming) {
                        state.dataPoint.singleValue?.let { rpm ->
                            sensorFusion.onCadenceUpdate(rpm)
                        }
                    }
                }
            }
        )

        // 4. Observe Power (Watts)
        streamJobs.add(
            serviceScope.launch {
                karooSystem.streamDataFlow(io.hammerhead.karooext.models.DataType.Type.POWER).collect { state ->
                    if (state is StreamState.Streaming) {
                        state.dataPoint.singleValue?.let { watts ->
                            sensorFusion.onPowerUpdate(watts)
                        }
                    }
                }
            }
        )

        // 5. FIT 1Hz Record Loop
        streamJobs.add(
            serviceScope.launch {
                while (isActive) {
                    delay(1000L)
                    if (isRecording) {
                        writeRecordMesg()
                    }
                }
            }
        )
    }

    override fun startFit(emitter: Emitter<FitEffect>) {
        fitEmitter = emitter
    }

    private fun writeRecordMesg() {
        val emitter = fitEmitter ?: return
        val metrics = sensorFusion.metrics.value

        val values = listOf(
            FieldValue(devFieldDecel, metrics.currentDecelMs2),
            FieldValue(devFieldPower, metrics.currentBrakingPowerWatts),
            FieldValue(devFieldIsBraking, if (metrics.isBraking) 1.0 else 0.0),
            FieldValue(devFieldTemp, metrics.estimatedTempCelsius),
            FieldValue(devFieldDES, metrics.descentEfficiencyScore.toDouble()),
            FieldValue(devFieldGripMargin, metrics.gripMarginPercent.toDouble()),
            FieldValue(devFieldMetabolic, metrics.metabolicKjBurned),
            FieldValue(devFieldPelotonSmoothness, metrics.pelotonSmoothnessIndex.toDouble()),
            FieldValue(devFieldApexRetention, metrics.apexSpeedRetentionPercent.toDouble()),
        )
        emitter.onNext(WriteToRecordMesg(values))
    }

    private fun writeSessionFitSummary() {
        val emitter = fitEmitter ?: return
        val metrics = sensorFusion.metrics.value

        val values = listOf(
            FieldValue(devFieldTotalEvents, metrics.totalBrakeEvents.toDouble()),
            FieldValue(devFieldMaxDecel, metrics.peakDecelMs2),
            FieldValue(devFieldTotalEnergy, metrics.totalEnergyKj),
            FieldValue(devFieldPadWear, metrics.padWearScore),
        )
        emitter.onNext(WriteToSessionMesg(values))
    }

    private fun triggerHardBrakeAlarm(peakDecel: Double, gForce: Double) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(2400, 120),
                        PlayBeepPattern.Tone(null, 40),
                        PlayBeepPattern.Tone(2800, 180),
                    )
                )
            )
        }

        val title = getString(R.string.alert_hard_braking_title)
        val decelStr = String.format(Locale.US, "%.1f %s", preferences.formatDecelValue(peakDecel), preferences.getDecelSymbol())
        val gStr = String.format(Locale.US, "%.2f G", gForce)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_hard_braking_detail), decelStr, gStr)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_hard_brake",
                icon = R.drawable.ic_brake_sense,
                title = title,
                detail = detail,
                autoDismissMs = 3000L,
                backgroundColor = android.R.color.holo_red_dark,
                textColor = android.R.color.white,
            )
        )
    }

    private fun triggerThermalWarning(brakeTypeName: String, tempCelsius: Double) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(1900, 250),
                        PlayBeepPattern.Tone(null, 80),
                        PlayBeepPattern.Tone(1900, 250),
                    )
                )
            )
        }

        val title = getString(R.string.alert_thermal_title)
        val tempDisplay = preferences.formatTemperature(tempCelsius)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_thermal_detail), brakeTypeName, tempDisplay)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_thermal_warning",
                icon = R.drawable.ic_wear,
                title = title,
                detail = detail,
                autoDismissMs = 4500L,
                backgroundColor = android.R.color.holo_orange_dark,
                textColor = android.R.color.white,
            )
        )
    }

    private fun triggerGlazingWarning(tempCelsius: Double, durationSec: Double) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(2200, 100),
                        PlayBeepPattern.Tone(null, 50),
                        PlayBeepPattern.Tone(2200, 100),
                        PlayBeepPattern.Tone(null, 50),
                        PlayBeepPattern.Tone(2200, 150),
                    )
                )
            )
        }

        val title = getString(R.string.alert_glazing_title)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_glazing_detail), durationSec, tempCelsius)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_glazing_warning",
                icon = R.drawable.ic_wear,
                title = title,
                detail = detail,
                autoDismissMs = 5000L,
                backgroundColor = android.R.color.holo_red_dark,
                textColor = android.R.color.white,
            )
        )
    }

    private fun triggerPadWearWarning(remainingPercent: Int) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(2000, 150),
                        PlayBeepPattern.Tone(null, 60),
                        PlayBeepPattern.Tone(2000, 150),
                    )
                )
            )
        }

        val title = getString(R.string.alert_pad_wear_title)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_pad_wear_detail), remainingPercent)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_pad_wear_warning",
                icon = R.drawable.ic_wear,
                title = title,
                detail = detail,
                autoDismissMs = 6000L,
                backgroundColor = android.R.color.holo_orange_dark,
                textColor = android.R.color.white,
            )
        )
    }

    private fun triggerGripLimitWarning(marginPct: Int, rollDeg: Double) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(2600, 80),
                        PlayBeepPattern.Tone(null, 40),
                        PlayBeepPattern.Tone(2600, 80),
                    )
                )
            )
        }

        val title = getString(R.string.alert_grip_limit_title)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_grip_limit_detail), marginPct, rollDeg)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_grip_limit_warning",
                icon = R.drawable.ic_corner_radar,
                title = title,
                detail = detail,
                autoDismissMs = 3500L,
                backgroundColor = android.R.color.holo_red_dark,
                textColor = android.R.color.white,
            )
        )
    }

    private fun triggerCornerBrakeWarning(distanceM: Int, currentKmh: Double, targetKmh: Double) {
        if (preferences.isBeeperEnabled()) {
            karooSystem.dispatch(
                PlayBeepPattern(
                    listOf(
                        PlayBeepPattern.Tone(2100, 140),
                        PlayBeepPattern.Tone(null, 50),
                        PlayBeepPattern.Tone(2500, 200),
                    )
                )
            )
        }

        val title = getString(R.string.alert_corner_braking_title)
        val detail = String.format(Locale.getDefault(), getString(R.string.alert_corner_braking_detail), distanceM, currentKmh, targetKmh)

        karooSystem.dispatch(
            InRideAlert(
                id = "karoobbrakers_corner_warning",
                icon = R.drawable.ic_corner_radar,
                title = title,
                detail = detail,
                autoDismissMs = 4000L,
                backgroundColor = android.R.color.holo_orange_dark,
                textColor = android.R.color.white,
            )
        )
    }

    override fun onDestroy() {
        streamJobs.forEach { it.cancel() }
        streamJobs.clear()
        sensorFusion.stop()
        karooSystem.disconnect()
        super.onDestroy()
    }
}
