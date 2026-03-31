package com.example.agristation1.data.taskDetails

import kotlinx.coroutines.flow.Flow

interface TaskDetailsRepository {
    fun getAllTasksStream(): Flow<List<TaskDetails>>

    fun getCompletedTasksStream(): Flow<List<TaskDetails>>

    fun getCancelledTasksStream(): Flow<List<TaskDetails>>

    fun getTaskByIdStream(taskId: Int): Flow<TaskDetails?>

    fun getTaskByAlertIdStream(alertId: Int): Flow<TaskDetails?>

    suspend fun markTaskAsOverdue(taskId: Int)

    suspend fun markTaskAsStarted(taskId: Int)

    suspend fun markTaskAsCompleted(taskId: Int)

    suspend fun unMarkTaskAsCompleted(taskId: Int)

    suspend fun deleteTask(taskId: Int)

    suspend fun insertTask(task: TaskDetails)

    suspend fun updateTask(task: TaskDetails)
}

class TaskDetailsOfflineRepository(
    private val taskDetailsDao: TaskDetailsDao
) : TaskDetailsRepository {

    override fun getAllTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getAllTasks()
    }

    override fun getCompletedTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getCompletedTasks()
    }

    override fun getCancelledTasksStream(): Flow<List<TaskDetails>> {
        return taskDetailsDao.getCancelledTasks()
    }

    override fun getTaskByIdStream(taskId: Int): Flow<TaskDetails?> {
        return taskDetailsDao.getTaskById(taskId)
    }

    override fun getTaskByAlertIdStream(alertId: Int): Flow<TaskDetails?> {
        return taskDetailsDao.getTaskByAlertId(alertId)
    }

    override suspend fun markTaskAsOverdue(taskId: Int) {
        taskDetailsDao.markTaskAsOverdue(taskId)
    }

    override suspend fun markTaskAsStarted(taskId: Int) {
        taskDetailsDao.markTaskAsStarted(taskId)
    }

    override suspend fun markTaskAsCompleted(taskId: Int) {
        taskDetailsDao.markTaskAsCompleted(taskId)
    }

    override suspend fun unMarkTaskAsCompleted(taskId: Int) {
        taskDetailsDao.unMarkTaskAsCompleted(taskId)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDetailsDao.deleteTask(taskId)
    }

    override suspend fun insertTask(task: TaskDetails) {
        taskDetailsDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskDetails) {
        taskDetailsDao.updateTask(task)
    }

}