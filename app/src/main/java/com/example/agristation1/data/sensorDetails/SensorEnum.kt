package com.example.agristation1.data.sensorDetails

enum class SensorBattery(val code: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    companion object {
        fun fromCode(code: Int): SensorBattery {
            return entries.find { it.code == code } ?: LOW
        }
    }
}

fun SensorBattery.toStringField(): String {
    return when(this) {
        SensorBattery.LOW -> "Low"
        SensorBattery.MEDIUM -> "Medium"
        SensorBattery.HIGH -> "High"
    }
}

enum class SensorState(val code: Int) {
    WORKING(0),
    WEAK_SIGNAL(1),
    BROKEN(2),
    UNKNOWN(3);

    companion object {
        fun fromCode(code: Int): SensorState {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}

fun SensorState.toStringField(): String {
    return when(this) {
        SensorState.WORKING -> "Working"
        SensorState.WEAK_SIGNAL -> "Weak Signal"
        SensorState.BROKEN -> "Broken"
        SensorState.UNKNOWN -> "Unknown"
    }
}