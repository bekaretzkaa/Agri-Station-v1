package com.example.agristation1.data.fieldDetails

import androidx.compose.ui.graphics.Color
import com.example.agristation1.data.AppColors

enum class FieldHealth(val code: Int) {
    HEALTHY(0),
    WARNING(1),
    CRITICAL(2);

    companion object {
        fun fromCode(code: Int): FieldHealth {
            return entries.find { it.code == code } ?: HEALTHY
        }
    }
}

fun FieldHealth.toContainerColor(): Color {
    return when (this) {
        FieldHealth.HEALTHY -> AppColors.green.c100
        FieldHealth.WARNING -> AppColors.yellow.c100
        FieldHealth.CRITICAL -> AppColors.red.c100
    }
}

fun FieldHealth.toContentColor(): Color {
    return when (this) {
        FieldHealth.HEALTHY -> AppColors.green.c800
        FieldHealth.WARNING -> AppColors.yellow.c800
        FieldHealth.CRITICAL -> AppColors.red.c800
    }
}

fun FieldHealth.toBorderColor(): Color {
    return when (this) {
        FieldHealth.HEALTHY -> AppColors.green.c200
        FieldHealth.WARNING -> AppColors.yellow.c200
        FieldHealth.CRITICAL -> AppColors.red.c200
    }
}

fun FieldHealth.toStringField(): String {
    return when (this) {
        FieldHealth.HEALTHY -> "HEALTHY"
        FieldHealth.WARNING -> "WARNING"
        FieldHealth.CRITICAL -> "CRITICAL"
    }
}

enum class FieldConnectivity(val code: Int) {
    ONLINE(0),
    PARTIAL(1),
    OFFLINE(2),
    UNKNOWN(3);

    companion object {
        fun fromCode(code: Int): FieldConnectivity {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}

fun FieldConnectivity.toContainerColor(): Color {
    return when (this) {
        FieldConnectivity.ONLINE -> AppColors.green.c100
        FieldConnectivity.PARTIAL -> AppColors.yellow.c100
        FieldConnectivity.OFFLINE -> AppColors.red.c100
        FieldConnectivity.UNKNOWN -> AppColors.gray.c100
    }
}

fun FieldConnectivity.toContentColor(): Color {
    return when (this) {
        FieldConnectivity.ONLINE -> AppColors.green.c800
        FieldConnectivity.PARTIAL -> AppColors.yellow.c800
        FieldConnectivity.OFFLINE -> AppColors.red.c800
        FieldConnectivity.UNKNOWN -> AppColors.gray.c800
    }
}

fun FieldConnectivity.toBorderColor(): Color {
    return when (this) {
        FieldConnectivity.ONLINE -> AppColors.green.c200
        FieldConnectivity.PARTIAL -> AppColors.yellow.c200
        FieldConnectivity.OFFLINE -> AppColors.red.c200
        FieldConnectivity.UNKNOWN -> AppColors.gray.c200
    }
}

fun FieldConnectivity.toStringField(): String {
    return when (this) {
        FieldConnectivity.ONLINE -> "ONLINE"
        FieldConnectivity.PARTIAL -> "PARTIAL"
        FieldConnectivity.OFFLINE -> "OFFLINE"
        FieldConnectivity.UNKNOWN -> "UNKNOWN"
    }
}

fun FieldConnectivity.toIconColor(): Color {
    return when (this) {
        FieldConnectivity.ONLINE -> AppColors.green.c600
        FieldConnectivity.PARTIAL -> AppColors.yellow.c600
        FieldConnectivity.OFFLINE -> AppColors.red.c600
        FieldConnectivity.UNKNOWN -> AppColors.gray.c600
    }
}

enum class FieldLifecycle(val code: Int) {
    ACTIVE(0),
    SETUP(1),
    MAINTENANCE(2),
    ARCHIVED(3);

    companion object {
        fun fromCode(code: Int): FieldLifecycle {
            return entries.find { it.code == code } ?: ARCHIVED
        }
    }
}

fun FieldLifecycle.toContainerColor(): Color {
    return when (this) {
        FieldLifecycle.ACTIVE -> AppColors.blue.c100
        FieldLifecycle.SETUP -> AppColors.purple.c100
        FieldLifecycle.MAINTENANCE -> AppColors.orange.c100
        FieldLifecycle.ARCHIVED -> AppColors.gray.c100
    }
}

fun FieldLifecycle.toContentColor(): Color {
    return when (this) {
        FieldLifecycle.ACTIVE -> AppColors.blue.c800
        FieldLifecycle.SETUP -> AppColors.purple.c800
        FieldLifecycle.MAINTENANCE -> AppColors.orange.c800
        FieldLifecycle.ARCHIVED -> AppColors.gray.c800
    }
}

fun FieldLifecycle.toBorderColor(): Color {
    return when (this) {
        FieldLifecycle.ACTIVE -> AppColors.blue.c200
        FieldLifecycle.SETUP -> AppColors.purple.c200
        FieldLifecycle.MAINTENANCE -> AppColors.orange.c200
        FieldLifecycle.ARCHIVED -> AppColors.gray.c200
    }
}

fun FieldLifecycle.toStringField(): String {
    return when (this) {
        FieldLifecycle.ACTIVE -> "active"
        FieldLifecycle.SETUP -> "setup"
        FieldLifecycle.MAINTENANCE -> "maintenance"
        FieldLifecycle.ARCHIVED -> "archived"
    }
}