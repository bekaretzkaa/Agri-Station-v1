package com.example.agristation1.network.alertNetwork

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class AlertPendingOperationType(val code: Int) {
    UPDATE_LIFECYCLE(0),
    DELETE_ALERT(1);
    companion object {
        fun fromCode(code: Int): AlertPendingOperationType? {
            return entries.find { it.code == code }
        }
    }
}

enum class AlertPendingOperationStatus(val code: Int) {
    PENDING(0),
    IN_FLIGHT(1),
    FAILED(2);

    companion object {
        fun fromCode(code: Int): AlertPendingOperationStatus? {
            return entries.find { it.code == code }
        }
    }
}

@Entity(tableName = "alert_pending_operations")
data class AlertPendingOperation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entity_id")
    val entityId: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    val payload: String,
    val operation: AlertPendingOperationType,
    val status: AlertPendingOperationStatus = AlertPendingOperationStatus.PENDING,
)