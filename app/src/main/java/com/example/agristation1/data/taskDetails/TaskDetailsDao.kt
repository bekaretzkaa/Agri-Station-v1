package com.example.agristation1.data.taskDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDetailsDao {
    @Query("SELECT * FROM task_details WHERE status NOT IN(2, 3) ORDER BY time_due ASC")
    fun getAllTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details WHERE status = 2 ORDER BY time_due ASC")
    fun getCompletedTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details WHERE status = 3 ORDER BY time_due ASC")
    fun getCancelledTasks(): Flow<List<TaskDetails>>

    @Query("SELECT * FROM task_details WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<TaskDetails?>

    @Query("SELECT * FROM task_details WHERE alert_id = :alertId")
    fun getTaskByAlertId(alertId: Int): Flow<TaskDetails?>

    @Query("UPDATE task_details SET status = 1 WHERE id = :taskId")
    suspend fun markTaskAsStarted(taskId: Int)

    @Query("UPDATE task_details SET status = 4 WHERE id = :taskId")
    suspend fun markTaskAsOverdue(taskId: Int)

    @Query("UPDATE task_details SET status = 2 WHERE id = :taskId")
    suspend fun markTaskAsCompleted(taskId: Int)

    @Query("UPDATE task_details SET status = 0 WHERE id = :taskId")
    suspend fun unMarkTaskAsCompleted(taskId: Int)

    @Query("DELETE FROM task_details WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Insert
    suspend fun insertTask(task: TaskDetails)

    @Update
    suspend fun updateTask(task: TaskDetails)
}