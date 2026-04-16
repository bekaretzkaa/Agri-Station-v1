package com.example.agristation1.data.taskDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDetailsDao {
    @Query("SELECT * FROM task_details ORDER BY time_due ASC")
    fun getAllTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details")
    suspend fun getAllTasksList(): List<TaskDetails>

    @Query("SELECT * FROM task_details WHERE status = 2 ORDER BY time_due ASC")
    fun getCompletedTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details WHERE status = 3 ORDER BY time_due ASC")
    fun getCancelledTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskDetails?>

    @Query("SELECT * FROM task_details WHERE alert_id = :alertId")
    fun getTaskByAlertId(alertId: Long): Flow<TaskDetails?>
    @Query("UPDATE task_details SET status = 4 WHERE id = :taskId")
    suspend fun markTaskAsOverdue(taskId: Long)

    @Query("DELETE FROM task_details WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Insert
    suspend fun insertTask(task: TaskDetails): Long

    @Update
    suspend fun updateTask(task: TaskDetails)

    @Query("UPDATE task_details SET notes = :notes WHERE id = :taskId")
    suspend fun updateTaskNotes(taskId: Long, notes: String)

    @Query("""
    UPDATE task_details
    SET alert_id = NULL, alert_deleted = 1
    WHERE alert_id = :alertId
    """)
    suspend fun detachFromDeletedAlert(alertId: Long)

    @Query("UPDATE task_details SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: Int)
}