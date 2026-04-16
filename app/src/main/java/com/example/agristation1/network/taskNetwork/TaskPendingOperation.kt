package com.example.agristation1.network.taskNetwork

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class TaskPendingOperationType(val code: Int) {
    UPDATE_NOTE(0),
    UPDATE_STATUS(1),
    UPDATE_TASK(2),
    CREATE_TASK(3),
    DELETE_TASK(4);

    companion object {
        fun fromCode(code: Int): TaskPendingOperationType? {
            return entries.find { it.code == code }
        }
    }
}

enum class TaskPendingOperationStatus(val code: Int) {
    PENDING(0),
    IN_FLIGHT(1),
    FAILED(2);

    companion object {
        fun fromCode(code: Int): TaskPendingOperationStatus? {
            return entries.find { it.code == code }
        }
    }
}

@Entity(tableName = "task_pending_operations")
data class TaskPendingOperation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entity_id")
    val entityId: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    val payload: String,
    val operation: TaskPendingOperationType,
    val status: TaskPendingOperationStatus = TaskPendingOperationStatus.PENDING,
)