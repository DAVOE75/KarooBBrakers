package com.davoe.karoobbrakers.data

import android.content.Context
import android.content.SharedPreferences
import com.davoe.karoobbrakers.model.BrakeType
import com.davoe.karoobbrakers.model.DecelUnit
import com.davoe.karoobbrakers.model.LeverFeel
import com.davoe.karoobbrakers.model.SurfaceProfile
import com.davoe.karoobbrakers.model.TemperatureUnit
import com.davoe.karoobbrakers.model.UnitSystem
import com.davoe.karoobbrakers.model.WeightUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class BrakePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _brakeTypeFlow = MutableStateFlow(loadBrakeType())
    val brakeTypeFlow: StateFlow<BrakeType> = _brakeTypeFlow.asStateFlow()

    private val _weightFlow = MutableStateFlow(loadTotalWeightKg())
    val weightFlow: StateFlow<Float> = _weightFlow.asStateFlow()

    private val _surfaceFlow = MutableStateFlow(loadSurfaceProfile())
    val surfaceFlow: StateFlow<SurfaceProfile> = _surfaceFlow.asStateFlow()

    private val _unitSystemFlow = MutableStateFlow(loadUnitSystem())
    val unitSystemFlow: StateFlow<UnitSystem> = _unitSystemFlow.asStateFlow()

    private val _temperatureUnitFlow = MutableStateFlow(loadTemperatureUnit())
    val temperatureUnitFlow: StateFlow<TemperatureUnit> = _temperatureUnitFlow.asStateFlow()

    private val _weightUnitFlow = MutableStateFlow(loadWeightUnit())
    val weightUnitFlow: StateFlow<WeightUnit> = _weightUnitFlow.asStateFlow()

    private val _decelUnitFlow = MutableStateFlow(loadDecelUnit())
    val decelUnitFlow: StateFlow<DecelUnit> = _decelUnitFlow.asStateFlow()

    private val _unitGForceFlow = MutableStateFlow(loadUnitGForce())
    val unitGForceFlow: StateFlow<Boolean> = _unitGForceFlow.asStateFlow()

    private val _hardBrakeAlertFlow = MutableStateFlow(loadHardBrakeAlertEnabled())
    val hardBrakeAlertFlow: StateFlow<Boolean> = _hardBrakeAlertFlow.asStateFlow()

    private val _beeperEnabledFlow = MutableStateFlow(loadBeeperEnabled())
    val beeperEnabledFlow: StateFlow<Boolean> = _beeperEnabledFlow.asStateFlow()

    private val _thermalWarningFlow = MutableStateFlow(loadThermalWarningEnabled())
    val thermalWarningFlow: StateFlow<Boolean> = _thermalWarningFlow.asStateFlow()

    private val _frontLeverFeelFlow = MutableStateFlow(loadFrontLeverFeel())
    val frontLeverFeelFlow: StateFlow<LeverFeel> = _frontLeverFeelFlow.asStateFlow()

    private val _rearLeverFeelFlow = MutableStateFlow(loadRearLeverFeel())
    val rearLeverFeelFlow: StateFlow<LeverFeel> = _rearLeverFeelFlow.asStateFlow()

    private val _pelotonModeFlow = MutableStateFlow(loadPelotonModeEnabled())
    val pelotonModeFlow: StateFlow<Boolean> = _pelotonModeFlow.asStateFlow()

    fun getBrakeType(): BrakeType = _brakeTypeFlow.value
    fun getTotalWeightKg(): Float = _weightFlow.value
    fun getSurfaceProfile(): SurfaceProfile = _surfaceFlow.value
    fun getUnitSystem(): UnitSystem = _unitSystemFlow.value
    fun getTemperatureUnit(): TemperatureUnit = _temperatureUnitFlow.value
    fun getWeightUnit(): WeightUnit = _weightUnitFlow.value
    fun getDecelUnit(): DecelUnit = _decelUnitFlow.value
    fun isUnitGForce(): Boolean = _unitGForceFlow.value
    fun isHardBrakeAlertEnabled(): Boolean = _hardBrakeAlertFlow.value
    fun getHardBrakeThresholdMs2(): Float = prefs.getFloat(KEY_HARD_BRAKE_THRESHOLD, 4.2f)
    fun isBeeperEnabled(): Boolean = _beeperEnabledFlow.value
    fun isThermalWarningEnabled(): Boolean = _thermalWarningFlow.value
    fun getFrontLeverFeel(): LeverFeel = _frontLeverFeelFlow.value
    fun getRearLeverFeel(): LeverFeel = _rearLeverFeelFlow.value
    fun isPelotonModeEnabled(): Boolean = _pelotonModeFlow.value

    fun setFrontLeverFeel(feel: LeverFeel) {
        prefs.edit().putString(KEY_FRONT_LEVER_FEEL, feel.id).apply()
        _frontLeverFeelFlow.value = feel
    }

    fun setRearLeverFeel(feel: LeverFeel) {
        prefs.edit().putString(KEY_REAR_LEVER_FEEL, feel.id).apply()
        _rearLeverFeelFlow.value = feel
    }

    fun setPelotonModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PELOTON_MODE, enabled).apply()
        _pelotonModeFlow.value = enabled
    }

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

    fun setUnitSystem(system: UnitSystem) {
        prefs.edit().putString(KEY_UNIT_SYSTEM, system.id).apply()
        _unitSystemFlow.value = system

        // Sync individual units to system defaults
        when (system) {
            UnitSystem.METRIC -> {
                setTemperatureUnit(TemperatureUnit.CELSIUS)
                setWeightUnit(WeightUnit.KG)
                setDecelUnit(DecelUnit.MS2)
                setUnitGForce(false)
            }
            UnitSystem.IMPERIAL -> {
                setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
                setWeightUnit(WeightUnit.LBS)
                setDecelUnit(DecelUnit.G_FORCE)
                setUnitGForce(true)
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_TEMP_UNIT, unit.id).apply()
        _temperatureUnitFlow.value = unit
    }

    fun setWeightUnit(unit: WeightUnit) {
        prefs.edit().putString(KEY_WEIGHT_UNIT, unit.id).apply()
        _weightUnitFlow.value = unit
    }

    fun setDecelUnit(unit: DecelUnit) {
        prefs.edit().putString(KEY_DECEL_UNIT, unit.id).apply()
        _decelUnitFlow.value = unit
        setUnitGForce(unit == DecelUnit.G_FORCE)
    }

    fun setUnitGForce(gForce: Boolean) {
        prefs.edit().putBoolean(KEY_UNIT_G_FORCE, gForce).apply()
        _unitGForceFlow.value = gForce
        if (gForce && _decelUnitFlow.value != DecelUnit.G_FORCE) {
            _decelUnitFlow.value = DecelUnit.G_FORCE
            prefs.edit().putString(KEY_DECEL_UNIT, DecelUnit.G_FORCE.id).apply()
        } else if (!gForce && _decelUnitFlow.value == DecelUnit.G_FORCE) {
            val fallback = if (_unitSystemFlow.value == UnitSystem.IMPERIAL) DecelUnit.FT_S2 else DecelUnit.MS2
            _decelUnitFlow.value = fallback
            prefs.edit().putString(KEY_DECEL_UNIT, fallback.id).apply()
        }
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

    // Format helpers
    fun formatTemperature(tempCelsius: Double): String {
        val unit = _temperatureUnitFlow.value
        val displayVal = unit.fromCelsius(tempCelsius)
        return String.format(Locale.US, "%.0f%s", displayVal, unit.symbol)
    }

    fun formatDecelValue(decelMs2: Double): Double {
        return _decelUnitFlow.value.fromMs2(decelMs2)
    }

    fun getDecelSymbol(): String = _decelUnitFlow.value.symbol
    fun getTemperatureSymbol(): String = _temperatureUnitFlow.value.symbol
    fun getWeightSymbol(): String = _weightUnitFlow.value.symbol

    private fun loadBrakeType(): BrakeType {
        val id = prefs.getString(KEY_BRAKE_TYPE, BrakeType.DISC.id) ?: BrakeType.DISC.id
        return BrakeType.fromId(id)
    }

    private fun loadTotalWeightKg(): Float = prefs.getFloat(KEY_TOTAL_WEIGHT, 80.0f)

    private fun loadSurfaceProfile(): SurfaceProfile {
        val id = prefs.getString(KEY_SURFACE_PROFILE, SurfaceProfile.ROAD.id) ?: SurfaceProfile.ROAD.id
        return SurfaceProfile.fromId(id)
    }

    private fun detectDefaultImperial(): Boolean {
        val country = Locale.getDefault().country.uppercase(Locale.ROOT)
        return country in listOf("US", "GB", "LR", "MM")
    }

    private fun loadUnitSystem(): UnitSystem {
        val defaultSystem = if (detectDefaultImperial()) UnitSystem.IMPERIAL else UnitSystem.METRIC
        val id = prefs.getString(KEY_UNIT_SYSTEM, defaultSystem.id) ?: defaultSystem.id
        return UnitSystem.fromId(id)
    }

    private fun loadTemperatureUnit(): TemperatureUnit {
        val defaultUnit = if (detectDefaultImperial()) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
        val id = prefs.getString(KEY_TEMP_UNIT, defaultUnit.id) ?: defaultUnit.id
        return TemperatureUnit.fromId(id)
    }

    private fun loadWeightUnit(): WeightUnit {
        val defaultUnit = if (detectDefaultImperial()) WeightUnit.LBS else WeightUnit.KG
        val id = prefs.getString(KEY_WEIGHT_UNIT, defaultUnit.id) ?: defaultUnit.id
        return WeightUnit.fromId(id)
    }

    private fun loadDecelUnit(): DecelUnit {
        val legacyG = prefs.getBoolean(KEY_UNIT_G_FORCE, detectDefaultImperial())
        val defaultUnit = if (legacyG) DecelUnit.G_FORCE else DecelUnit.MS2
        val id = prefs.getString(KEY_DECEL_UNIT, defaultUnit.id) ?: defaultUnit.id
        return DecelUnit.fromId(id)
    }

    private fun loadUnitGForce(): Boolean {
        return prefs.getBoolean(KEY_UNIT_G_FORCE, detectDefaultImperial())
    }

    private fun loadHardBrakeAlertEnabled(): Boolean = prefs.getBoolean(KEY_HARD_BRAKE_ALERT, true)
    private fun loadBeeperEnabled(): Boolean = prefs.getBoolean(KEY_BEEPER_ENABLED, true)
    private fun loadThermalWarningEnabled(): Boolean = prefs.getBoolean(KEY_THERMAL_WARNING, true)

    private fun loadFrontLeverFeel(): LeverFeel {
        val id = prefs.getString(KEY_FRONT_LEVER_FEEL, LeverFeel.STANDARD.id) ?: LeverFeel.STANDARD.id
        return LeverFeel.fromId(id)
    }

    private fun loadRearLeverFeel(): LeverFeel {
        val id = prefs.getString(KEY_REAR_LEVER_FEEL, LeverFeel.PROGRESSIVE_LONG.id) ?: LeverFeel.PROGRESSIVE_LONG.id
        return LeverFeel.fromId(id)
    }

    private fun loadPelotonModeEnabled(): Boolean = prefs.getBoolean(KEY_PELOTON_MODE, true)

    companion object {
        private const val PREFS_NAME = "karoobbrakers_prefs"
        private const val KEY_BRAKE_TYPE = "brake_type"
        private const val KEY_TOTAL_WEIGHT = "total_weight_kg"
        private const val KEY_SURFACE_PROFILE = "surface_profile"
        private const val KEY_UNIT_SYSTEM = "unit_system"
        private const val KEY_TEMP_UNIT = "temp_unit"
        private const val KEY_WEIGHT_UNIT = "weight_unit"
        private const val KEY_DECEL_UNIT = "decel_unit"
        private const val KEY_UNIT_G_FORCE = "unit_g_force"
        private const val KEY_HARD_BRAKE_ALERT = "hard_brake_alert"
        private const val KEY_HARD_BRAKE_THRESHOLD = "hard_brake_threshold"
        private const val KEY_BEEPER_ENABLED = "beeper_enabled"
        private const val KEY_THERMAL_WARNING = "thermal_warning"
        private const val KEY_FRONT_LEVER_FEEL = "front_lever_feel"
        private const val KEY_REAR_LEVER_FEEL = "rear_lever_feel"
        private const val KEY_PELOTON_MODE = "peloton_mode"
    }
}
