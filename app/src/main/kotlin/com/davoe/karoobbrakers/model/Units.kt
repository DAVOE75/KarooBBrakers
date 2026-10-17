package com.davoe.karoobbrakers.model

enum class UnitSystem(val id: String) {
    METRIC("metric"),       // Germany, Spain, France, Italy, Portugal, rest of Europe
    IMPERIAL("imperial");   // UK & USA

    companion object {
        fun fromId(id: String): UnitSystem =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: METRIC
    }
}

enum class TemperatureUnit(val id: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F");

    fun fromCelsius(tempC: Double): Double = when (this) {
        CELSIUS -> tempC
        FAHRENHEIT -> tempC * 1.8 + 32.0
    }

    fun toCelsius(displayVal: Double): Double = when (this) {
        CELSIUS -> displayVal
        FAHRENHEIT -> (displayVal - 32.0) / 1.8
    }

    companion object {
        fun fromId(id: String): TemperatureUnit =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CELSIUS
    }
}

enum class WeightUnit(val id: String, val symbol: String) {
    KG("kg", "kg"),
    LBS("lbs", "lbs");

    fun fromKg(kg: Float): Float = when (this) {
        KG -> kg
        LBS -> kg * 2.20462262f
    }

    fun toKg(displayVal: Float): Float = when (this) {
        KG -> displayVal
        LBS -> displayVal / 2.20462262f
    }

    companion object {
        fun fromId(id: String): WeightUnit =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: KG
    }
}

enum class DecelUnit(val id: String, val symbol: String) {
    MS2("ms2", "m/s²"),
    G_FORCE("g", "G"),
    FT_S2("fts2", "ft/s²");

    fun fromMs2(ms2: Double): Double = when (this) {
        MS2 -> ms2
        G_FORCE -> ms2 / 9.80665
        FT_S2 -> ms2 * 3.2808399
    }

    companion object {
        fun fromId(id: String): DecelUnit =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MS2
    }
}
