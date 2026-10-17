package com.davoe.karoobbrakers.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.extension.streamDataFlow
import com.davoe.karoobbrakers.sensor.BrakeSensorFusion
import com.davoe.karoobbrakers.ui.theme.KarooBBrakersTheme
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferences: BrakePreferences
    private lateinit var sensorFusion: BrakeSensorFusion
    private val karooSystem by lazy { KarooSystemService(this) }
    private val scope = CoroutineScope(Dispatchers.Main)
    private val streamJobs = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = BrakePreferences(this)
        sensorFusion = BrakeSensorFusion(this, preferences)

        setContent {
            KarooBBrakersTheme {
                val metrics by sensorFusion.metrics.collectAsState()
                MainScreen(
                    preferences = preferences,
                    metrics = metrics,
                )
            }
        }

        karooSystem.connect {
            // 1. Observe Speed (m/s)
            streamJobs.add(
                scope.launch {
                    karooSystem.streamDataFlow(DataType.Type.SPEED).collect { state ->
                        if (state is StreamState.Streaming) {
                            state.dataPoint.singleValue?.let { speedMs ->
                                sensorFusion.onSpeedUpdate(speedMs)
                            }
                        }
                    }
                }
            )

            // 2. Observe Cadence (RPM)
            streamJobs.add(
                scope.launch {
                    karooSystem.streamDataFlow(DataType.Type.CADENCE).collect { state ->
                        if (state is StreamState.Streaming) {
                            state.dataPoint.singleValue?.let { rpm ->
                                sensorFusion.onCadenceUpdate(rpm)
                            }
                        }
                    }
                }
            )

            // 3. Observe Power (Watts)
            streamJobs.add(
                scope.launch {
                    karooSystem.streamDataFlow(DataType.Type.POWER).collect { state ->
                        if (state is StreamState.Streaming) {
                            state.dataPoint.singleValue?.let { watts ->
                                sensorFusion.onPowerUpdate(watts)
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        sensorFusion.start()
    }

    override fun onPause() {
        sensorFusion.stop()
        super.onPause()
    }

    override fun onDestroy() {
        streamJobs.forEach { it.cancel() }
        streamJobs.clear()
        karooSystem.disconnect()
        super.onDestroy()
    }
}
