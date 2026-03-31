package com.example.agristation1.data.taskDetails

import androidx.compose.ui.graphics.Color
import com.example.agristation1.data.AppColors

enum class TaskStatus(val code: Int) {
    OPEN(0),
    IN_PROGRESS(1),
    COMPLETED(2),
    CANCELLED(3),
    OVERDUE(4);

    companion object {
        fun fromCode(code: Int): TaskStatus {
            return entries.find { it.code == code } ?: OPEN
        }
    }
}

fun TaskStatus.toContainerColor(): Color {
    return when (this) {
        TaskStatus.OPEN -> AppColors.gray.c100
        TaskStatus.IN_PROGRESS -> AppColors.blue.c100
        TaskStatus.COMPLETED -> AppColors.green.c100
        TaskStatus.CANCELLED -> AppColors.gray.c100
        TaskStatus.OVERDUE -> AppColors.red.c100
    }
}

fun TaskStatus.toContentColor(): Color {
    return when (this) {
        TaskStatus.OPEN -> AppColors.gray.c800
        TaskStatus.IN_PROGRESS -> AppColors.blue.c800
        TaskStatus.COMPLETED -> AppColors.green.c800
        TaskStatus.CANCELLED -> AppColors.gray.c800
        TaskStatus.OVERDUE -> AppColors.red.c800
    }
}

fun TaskStatus.toBorderColor(): Color {
    return when (this) {
        TaskStatus.OPEN -> AppColors.gray.c200
        TaskStatus.IN_PROGRESS -> AppColors.blue.c200
        TaskStatus.COMPLETED -> AppColors.green.c200
        TaskStatus.CANCELLED -> AppColors.gray.c200
        TaskStatus.OVERDUE -> AppColors.red.c200
    }
}

fun TaskStatus.toStringField(): String {
    return when (this) {
        TaskStatus.OPEN -> "OPEN"
        TaskStatus.IN_PROGRESS -> "IN PROGRESS"
        TaskStatus.COMPLETED -> "COMPLETED"
        TaskStatus.CANCELLED -> "CANCELLED"
        TaskStatus.OVERDUE -> "OVERDUE"
    }
}

enum class TaskPriority(val code: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    companion object {
        fun fromCode(code: Int): TaskPriority {
            return entries.find { it.code == code } ?: LOW
        }
    }
}

fun TaskPriority.toContainerColor(): Color {
    return when (this) {
        TaskPriority.LOW -> AppColors.blue.c100
        TaskPriority.MEDIUM -> AppColors.yellow.c100
        TaskPriority.HIGH -> AppColors.red.c100
    }
}

fun TaskPriority.toContentColor(): Color {
    return when (this) {
        TaskPriority.LOW -> AppColors.blue.c800
        TaskPriority.MEDIUM -> AppColors.yellow.c800
        TaskPriority.HIGH -> AppColors.red.c800
    }
}

fun TaskPriority.toBorderColor(): Color {
    return when (this) {
        TaskPriority.LOW -> AppColors.blue.c200
        TaskPriority.MEDIUM -> AppColors.yellow.c200
        TaskPriority.HIGH -> AppColors.red.c200
    }
}

fun TaskPriority.toStringField(): String {
    return when (this) {
        TaskPriority.LOW -> "LOW"
        TaskPriority.MEDIUM -> "MEDIUM"
        TaskPriority.HIGH -> "HIGH"
    }
}

enum class TaskType(val code: Int) {
    SOIL_MOISTURE(0),
    SOIL_TEMPERATURE(1),
    AIR_TEMPERATURE(2),
    AIR_HUMIDITY(3),
    LUX(4),
    SENSOR(5),
    GATE(6),
    UNKNOWN(7);

    companion object {
        fun fromCode(code: Int): TaskType {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}

fun TaskType.toStringField(): String {
    return when(this) {
        TaskType.SOIL_MOISTURE -> "Soil Moisture"
        TaskType.SOIL_TEMPERATURE -> "Soil Temperature"
        TaskType.AIR_TEMPERATURE -> "Air Temperature"
        TaskType.AIR_HUMIDITY -> "Air Humidity"
        TaskType.LUX -> "Lux"
        TaskType.GATE -> "Gate"
        TaskType.SENSOR -> "Sensor"
        TaskType.UNKNOWN -> "Unknown"
    }
}