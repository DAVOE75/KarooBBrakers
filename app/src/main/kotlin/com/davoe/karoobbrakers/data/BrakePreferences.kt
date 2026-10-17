package com.davoe.karoobbrakers.data

import android.content.Context
import android.content.SharedPreferences
import com.davoe.karoobbrakers.model.BrakeType
import com.davoe.karoobbrakers.model.SurfaceProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BrakePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _brakeTypeFlow = MutableStateFlow(loadBrakeType())
    val brakeTypeFlow: StateFlow<BrakeType> = _brakeTypeFlow.asStateFlow()

    private val _weightFlow = MutableStateFlow(loadTotalWeightKg())
    val weightFlow: StateFlow<Float> = _weightFlow.asStateFlow()

    private val _surfaceFlow = MutableStateFlow(loadSurfaceProfile())
    val surfaceFlow: StateFlow<SurfaceProfile> = _surfaceFlow.asStateFlow()

    private val _unitGForceFlow = MutableStateFlow(loadUnitGForce())
    val unitGForceFlow: StateFlow<Boolean> = _unitGForceFlow.asStateFlow()

    private val _hardBrakeAlertFlow = MutableStateFlow(loadHardBrakeAlertEnabled())
    val hardBrakeAlertFlow: StateFlow<Boolean> = _hardBrakeAlertFlow.asStateFlow()

    private val _beeperEnabledFlow = MutableStateFlow(loadBeeperEnabled())
    val beeperEnabledFlow: StateFlow<Boolean> = _beeperEnabledFlow.asStateFlow()

    private val _thermalWarningFlow = MutableStateFlow(loadThermalWarningEnabled())
    val thermalWarningFlow: StateFlow<Boolean> = _thermalWarningFlow.asStateFlow()

    fun getBrakeType(): BrakeType = _brakeTypeFlow.value
    fun getTotalWeightKg(): Float = _weightFlow.value
    fun getSurfaceProfile(): SurfaceProfile = _surfaceFlow.value
    fun isUnitGForce(): Boolean = _unitGForceFlow.value
    fun isHardBrakeAlertEnabled(): Boolean = _hardBrakeAlertFlow.value
    fun getHardBrakeThresholdMs2(): Float = prefs.getFloat(KEY_HARD_BRAKE_THRESHOLD, 4.2f)
    fun isBeeperEnabled(): Boolean = _beeperEnabledFlow.value
    fun isThermalWarningEnabled(): Boolean = _thermalWarningFlow.value

    fun setBrakeType(brakeType: BrakeType) {
        prefs.edit().putString(KEY_BRAKE_TYPE, brakeType.id).apply()
        _brakeTypeFlow.value = brakeType
    }

    fun setTotalWeightKg(weightKg: Float) {
        val clamped = weightKg.coerceIn(40f, 160f)
        prefs.edit().putFloat(KEY_TOTAL_WEIGHT, clamped).apply()
        _weightFlow.value = clamped
    }

    fun setSurfaceProfile(profile: SurfaceProfile) {
        prefs.edit().putString(KEY_SURFACE_PROFILE, profile.id).apply()
        _surfaceFlow.value = profile
    }

    fun setUnitGForce(gForce: Boolean) {
        prefs.edit().putBoolean(KEY_UNIT_G_FORCE, gForce).apply()
        _unitGForceFlow.value = gForce
    }

    fun setHardBrakeAlertEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HARD_BRAKE_ALERT, enabled).apply()
        _hardBrakeAlertFlow.value = enabled
    }

    fun setBeeperEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BEEPER_ENABLED, enabled).apply()
        _beeperEnabledFlow.value = enabled
    }

    fun setThermalWarningEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_THERMAL_WARNING, enabled).apply()
        _thermalWarningFlow.value = enabled
    }

    private fun loadBrakeType(): BrakeType {
        val id = prefs.getString(KEY_BRAKE_TYPE, BrakeType.DISC.id) ?: BrakeType.DISC.id
        return BrakeType.fromId(id)
    }

    private fun loadTotalWeightKg(): Float = prefs.getFloat(KEY_TOTAL_WEIGHT, 80.0f)

    private fun loadSurfaceProfile(): SurfaceProfile {
        val id = prefs.getString(KEY_SURFACE_PROFILE, SurfaceProfile.ROAD.id) ?: SurfaceProfile.ROAD.id
        return SurfaceProfile.fromId(id)
    }

    private fun loadUnitGForce(): Boolean = prefs.getBoolean(KEY_UNIT_G_FORCE, false)
    private fun loadHardBrakeAlertEnabled(): Boolean = prefs.getBoolean(KEY_HARD_BRAKE_ALERT, true)
    private fun loadBeeperEnabled(): Boolean = prefs.getBoolean(KEY_BEEPER_ENABLED, true)
    private fun loadThermalWarningEnabled(): Boolean = prefs.getBoolean(KEY_THERMAL_WARNING, true)

    companion object {
        private const val PREFS_NAME = "karoobbrakers_prefs"
        private const val KEY_BRAKE_TYPE = "brake_type"
        private const val KEY_TOTAL_WEIGHT = "total_weight_kg"
        private const val KEY_SURFACE_PROFILE = "surface_profile"
        private const val KEY_UNIT_G_FORCE = "unit_g_force"
        private const val KEY_HARD_BRAKE_ALERT = "hard_brake_alert"
        private const val KEY_HARD_BRAKE_THRESHOLD = "hard_brake_threshold"
        private const val KEY_BEEPER_ENABLED = "beeper_enabled"
        private const val KEY_THERMAL_WARNING = "thermal_warning"
    }
}
