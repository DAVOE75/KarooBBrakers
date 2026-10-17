package com.davoe.karoobbrakers.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.davoe.karoobbrakers.data.BrakePreferences
import com.davoe.karoobbrakers.sensor.BrakeSensorFusion
import com.davoe.karoobbrakers.ui.theme.KarooBBrakersTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferences: BrakePreferences
    private lateinit var sensorFusion: BrakeSensorFusion

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
    }

    override fun onResume() {
        super.onResume()
        sensorFusion.start()
    }

    override fun onPause() {
        sensorFusion.stop()
        super.onPause()
    }
}
