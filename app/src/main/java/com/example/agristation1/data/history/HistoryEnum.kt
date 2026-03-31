package com.example.agristation1.data.history

enum class HistoryStatus(val code: Int) {
    SUCCESS(0),
    LOW_BATTERY(1),
    SENSOR_FAILURE(2),
    WEAK_SIGNAL(3),
    UNKNOWN(4);

    companion object {
        fun fromCode(code: Int): HistoryStatus {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}