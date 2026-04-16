package com.example.agristation1.network.taskNetwork

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskPendingOperationDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(operation: TaskPendingOperation)

    @Query("SELECT * FROM task_pending_operations WHERE status != 2 ORDER BY created_at ASC")
    suspend fun getAllPending(): List<TaskPendingOperation>

    @Query("SELECT * FROM task_pending_operations WHERE entity_id = :taskId")
    suspend fun getByTaskId(taskId: Long): List<TaskPendingOperation>

    @Query("UPDATE task_pending_operations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: TaskPendingOperationStatus)

    @Query("UPDATE task_pending_operations SET status = 2 WHERE id = :id")
    suspend fun markFailed(id: Long)

    @Query("DELETE FROM task_pending_operations WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM task_pending_operations WHERE entity_id = :taskId")
    suspend fun deleteByTaskId(taskId: Long)

    @Query("SELECT COUNT(*) FROM task_pending_operations WHERE entity_id = :taskId AND status = 0")
    suspend fun hasPendingForTask(taskId: Long): Int

    @Query("""
        DELETE FROM task_pending_operations
        WHERE entity_id = :taskId AND operation = :operation
    """)
    suspend fun deleteByTaskIdAndOperation(
        taskId: Long,
        operation: TaskPendingOperationType
    )

    @Query("SELECT COUNT(*) FROM task_pending_operations WHERE status != 2")
    suspend fun countAllTaskPending(): Int
}