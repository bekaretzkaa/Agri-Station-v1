package com.example.agristation1.data.taskDetails

import kotlinx.coroutines.flow.Flow

interface TaskDetailsRepository {
    fun getAllTasksStream(): Flow<List<TaskDetails>>

    suspend fun getAllTasksList(): List<TaskDetails>

    fun getCompletedTasksStream(): Flow<List<TaskDetails>>

    fun getCancelledTasksStream(): Flow<List<TaskDetails>>

    fun getTaskByIdStream(taskId: Long): Flow<TaskDetails?>

    fun getTaskByAlertIdStream(alertId: Long): Flow<TaskDetails?>

    suspend fun markTaskAsOverdue(taskId: Long)

    suspend fun deleteTask(taskId: Long)

    suspend fun insertTask(task: TaskDetails): Long

    suspend fun updateTask(task: TaskDetails)

    suspend fun updateTaskNote(taskId: Long, notes: String)

    suspend fun updateTaskStatus(taskId: Long, status: Int)
}

class TaskDetailsOfflineRepository(
    private val taskDetailsDao: TaskDetailsDao
) : TaskDetailsRepository {

    override fun getAllTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getAllTasks()
    }

    override suspend fun getAllTasksList(): List<TaskDetails> {
        return taskDetailsDao.getAllTasksList()
    }

    override fun getCompletedTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getCompletedTasks()
    }

    override fun getCancelledTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getCancelledTasks()
    }

    override fun getTaskByIdStream(taskId: Long): Flow<TaskDetails?> {
        return taskDetailsDao.getTaskById(taskId)
    }

    override fun getTaskByAlertIdStream(alertId: Long): Flow<TaskDetails?> {
        return taskDetailsDao.getTaskByAlertId(alertId)
    }

    override suspend fun markTaskAsOverdue(taskId: Long) {
        taskDetailsDao.markTaskAsOverdue(taskId)
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDetailsDao.deleteTask(taskId)
    }

    override suspend fun insertTask(task: TaskDetails): Long {
        return taskDetailsDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskDetails) {
        taskDetailsDao.updateTask(task)
    }

    suspend fun detachFromDeletedAlert(alertId: Long) {
        taskDetailsDao.detachFromDeletedAlert(alertId)
    }

    override suspend fun updateTaskNote(taskId: Long, notes: String) {
        taskDetailsDao.updateTaskNotes(taskId, notes)
    }

    override suspend fun updateTaskStatus(taskId: Long, status: Int) {
        taskDetailsDao.updateTaskStatus(taskId, status)
    }
}