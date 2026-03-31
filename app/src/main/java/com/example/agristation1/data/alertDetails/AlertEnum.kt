package com.example.agristation1.data.alertDetails

import androidx.compose.ui.graphics.Color
import com.example.agristation1.data.AppColors

enum class AlertLifecycle(val code: Int) {
    OPEN(0),
    ACKNOWLEDGED(1),
    RESOLVED(2),
    DISMISSED(3);

    companion object {
        fun fromCode(code: Int): AlertLifecycle {
            return entries.find { it.code == code } ?: DISMISSED
        }
    }
}

fun AlertLifecycle.toContainerColor(): Color {
    return when (this) {
        AlertLifecycle.OPEN -> AppColors.red.c100
        AlertLifecycle.ACKNOWLEDGED -> AppColors.yellow.c100
        AlertLifecycle.RESOLVED -> AppColors.green.c100
        AlertLifecycle.DISMISSED -> AppColors.gray.c100
    }
}

fun AlertLifecycle.toContentColor(): Color {
    return when (this) {
        AlertLifecycle.OPEN -> AppColors.red.c800
        AlertLifecycle.ACKNOWLEDGED -> AppColors.yellow.c800
        AlertLifecycle.RESOLVED -> AppColors.green.c800
        AlertLifecycle.DISMISSED -> AppColors.gray.c800
    }
}

fun AlertLifecycle.toBorderColor(): Color {
    return when (this) {
        AlertLifecycle.OPEN -> AppColors.red.c200
        AlertLifecycle.ACKNOWLEDGED -> AppColors.yellow.c200
        AlertLifecycle.RESOLVED -> AppColors.green.c200
        AlertLifecycle.DISMISSED -> AppColors.gray.c200
    }
}

fun AlertLifecycle.toStringField(): String {
    return when (this) {
        AlertLifecycle.OPEN -> "OPEN"
        AlertLifecycle.ACKNOWLEDGED -> "ACKNOWLEDGED"
        AlertLifecycle.RESOLVED -> "RESOLVED"
        AlertLifecycle.DISMISSED -> "DISMISSED"
    }
}

enum class AlertSeverity(val code: Int) {
    INFO(0),
    NOTICE(1),
    WARNING(2),
    CRITICAL(3);

    companion object {
        fun fromCode(code: Int): AlertSeverity {
            return entries.find { it.code == code } ?: INFO
        }
    }
}

fun AlertSeverity.toContainerColor(): Color {
    return when (this) {
        AlertSeverity.INFO -> AppColors.gray.c100
        AlertSeverity.NOTICE -> AppColors.yellow.c100
        AlertSeverity.WARNING -> AppColors.orange.c100
        AlertSeverity.CRITICAL -> AppColors.red.c100
    }
}

fun AlertSeverity.toContentColor(): Color {
    return when (this) {
        AlertSeverity.INFO -> AppColors.gray.c800
        AlertSeverity.NOTICE -> AppColors.yellow.c800
        AlertSeverity.WARNING -> AppColors.orange.c800
        AlertSeverity.CRITICAL -> AppColors.red.c800
    }
}

fun AlertSeverity.toBorderColor(): Color {
    return when (this) {
        AlertSeverity.INFO -> AppColors.gray.c200
        AlertSeverity.NOTICE -> AppColors.yellow.c200
        AlertSeverity.WARNING -> AppColors.orange.c200
        AlertSeverity.CRITICAL -> AppColors.red.c200
    }
}

fun AlertSeverity.toStringField(): String {
    return when (this) {
        AlertSeverity.INFO -> "INFO"
        AlertSeverity.NOTICE -> "NOTICE"
        AlertSeverity.WARNING -> "WARNING"
        AlertSeverity.CRITICAL -> "CRITICAL"
    }
}

fun AlertSeverity.toIconColor(): Color {
    return when (this) {
        AlertSeverity.INFO -> AppColors.gray.c600
        AlertSeverity.NOTICE -> AppColors.yellow.c600
        AlertSeverity.WARNING -> AppColors.orange.c600
        AlertSeverity.CRITICAL -> AppColors.red.c600
    }
}

enum class AlertType(val code: Int) {
    SOIL_MOISTURE(0),
    SOIL_TEMPERATURE(1),
    AIR_TEMPERATURE(2),
    AIR_HUMIDITY(3),
    LUX(4),
    SENSOR(5),
    GATE(6),
    UNKNOWN(7);

    companion object {
        fun fromCode(code: Int): AlertType {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}

fun AlertType.toStringField(): String {
    return when(this) {
        AlertType.SOIL_MOISTURE -> "soil moisture"
        AlertType.SOIL_TEMPERATURE -> "soil temperature"
        AlertType.AIR_TEMPERATURE -> "air temperature"
        AlertType.AIR_HUMIDITY -> "air humidity"
        AlertType.LUX -> "lux"
        AlertType.SENSOR -> "sensor"
        AlertType.GATE -> "gate"
        AlertType.UNKNOWN -> "unknown"
    }
}

enum class AlertVerification(val code: Int) {
    UNVERIFIED(0),
    CONFIRMED(1),
    FALSE_POSITIVE(2);

    companion object {
        fun fromCode(code: Int): AlertVerification {
            return entries.find { it.code == code } ?: UNVERIFIED
        }
    }
}