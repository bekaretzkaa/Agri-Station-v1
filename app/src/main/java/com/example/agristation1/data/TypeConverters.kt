package com.example.agristation1.data

import androidx.room.TypeConverter
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.alertDetails.AlertType
import com.example.agristation1.data.alertDetails.AlertVerification
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.chatDetails.MessageStatus
import com.example.agristation1.data.fieldDetails.FieldConnectivity
import com.example.agristation1.data.fieldDetails.FieldHealth
import com.example.agristation1.data.fieldDetails.FieldLifecycle
import com.example.agristation1.data.sensorDetails.SensorBattery
import com.example.agristation1.data.sensorDetails.SensorState
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.network.alertNetwork.AlertPendingOperationStatus
import com.example.agristation1.network.alertNetwork.AlertPendingOperationType
import com.example.agristation1.network.taskNetwork.TaskPendingOperationStatus
import com.example.agristation1.network.taskNetwork.TaskPendingOperationType
import java.time.Instant
import java.time.LocalDate

class FieldConverters {
    @TypeConverter
    fun fromFieldHealth(value: FieldHealth): Int = value.code

    @TypeConverter
    fun toFieldHealth(value: Int): FieldHealth = FieldHealth.fromCode(value)

    @TypeConverter
    fun fromFieldConnectivity(value: FieldConnectivity): Int = value.code

    @TypeConverter
    fun toFieldConnectivity(value: Int): FieldConnectivity = FieldConnectivity.fromCode(value)

    @TypeConverter
    fun fromFieldLifecycle(value: FieldLifecycle): Int = value.code

    @TypeConverter
    fun toFieldLifecycle(value: Int): FieldLifecycle = FieldLifecycle.fromCode(value)
}

class AlertConverters {
    @TypeConverter
    fun fromAlertLifecycle(value: AlertLifecycle): Int = value.code

    @TypeConverter
    fun toAlertLifecycle(value: Int): AlertLifecycle = AlertLifecycle.fromCode(value)

    @TypeConverter
    fun fromAlertSeverity(value: AlertSeverity): Int = value.code

    @TypeConverter
    fun toAlertSeverity(value: Int): AlertSeverity = AlertSeverity.fromCode(value)

    @TypeConverter
    fun fromAlertType(value: AlertType): Int = value.code

    @TypeConverter
    fun toAlertType(value: Int): AlertType = AlertType.fromCode(value)

    @TypeConverter
    fun fromAlertVerification(value: AlertVerification): Int = value.code

    @TypeConverter
    fun toAlertVerification(value: Int): AlertVerification = AlertVerification.fromCode(value)
}

class TaskConverters {
    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): Int = value.code

    @TypeConverter
    fun toTaskStatus(value: Int): TaskStatus = TaskStatus.fromCode(value)

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): Int = value.code

    @TypeConverter
    fun toTaskPriority(value: Int): TaskPriority = TaskPriority.fromCode(value)

    @TypeConverter
    fun fromTaskType(value: TaskType): Int = value.code

    @TypeConverter
    fun toTaskType(value: Int): TaskType = TaskType.fromCode(value)
}

class TimeConverters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Instant? {
        return value?.let {
            Instant.ofEpochSecond(it)
        }
    }

    @TypeConverter
    fun toTimeStamp(value: Instant?): Long? {
        return value?.epochSecond
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}

class MessageConverters {
    @TypeConverter
    fun fromMessageRole(value: MessageRole): Int = value.code

    @TypeConverter
    fun toMessageRole(value: Int): MessageRole = MessageRole.fromCode(value)

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus): Int = value.code

    @TypeConverter
    fun toMessageStatus(value: Int): MessageStatus = MessageStatus.fromCode(value)
}

class PendingOperationConverters {
    @TypeConverter
    fun fromAlertPendingOperationType(value: AlertPendingOperationType): Int = value.code

    @TypeConverter
    fun toAlertPendingOperationType(value: Int): AlertPendingOperationType? = AlertPendingOperationType.fromCode(value)

    @TypeConverter
    fun fromAlertPendingOperationStatus(value: AlertPendingOperationStatus): Int = value.code

    @TypeConverter
    fun toAlertPendingOperationStatus(value: Int): AlertPendingOperationStatus? = AlertPendingOperationStatus.fromCode(value)


    @TypeConverter
    fun fromTaskPendingOperationType(value: TaskPendingOperationType): Int = value.code

    @TypeConverter
    fun toTaskPendingOperationType(value: Int): TaskPendingOperationType? = TaskPendingOperationType.fromCode(value)

    @TypeConverter
    fun fromTaskPendingOperationStatus(value: TaskPendingOperationStatus): Int = value.code

    @TypeConverter
    fun toTaskPendingOperationStatus(value: Int): TaskPendingOperationStatus? = TaskPendingOperationStatus.fromCode(value)
}

class SensorConverters {
    @TypeConverter
    fun fromSensorBattery(value: SensorBattery): Int = value.code

    @TypeConverter
    fun toSensorBattery(value: Int): SensorBattery = SensorBattery.fromCode(value)

    @TypeConverter
    fun fromSensorState(value: SensorState): Int = value.code

    @TypeConverter
    fun toSensorState(value: Int): SensorState = SensorState.fromCode(value)
}