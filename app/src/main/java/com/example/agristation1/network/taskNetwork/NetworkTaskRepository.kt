package com.example.agristation1.network.taskNetwork

interface NetworkTaskRepository {
    suspend fun getTasks(): TasksNetwork

    suspend fun updateStatus(taskId: Long, status: Int): TaskSyncResponse

    suspend fun updateNote(taskId: Long, note: String): TaskSyncResponse

    suspend fun updateTask(taskId: Long, task: TaskDetailsNetwork): TaskSyncResponse

    suspend fun deleteTask(taskId: Long): TaskSyncResponse

    suspend fun createTask(task: TaskDetailsNetwork): TaskSyncResponse
}

class NetworkTaskRepositoryImpl(
    private val taskApiService: TaskApiService
) : NetworkTaskRepository {

    override suspend fun getTasks(): TasksNetwork {
        return taskApiService.getTasks()
    }

    override suspend fun updateStatus(taskId: Long, status: Int): TaskSyncResponse {
        return taskApiService.updateStatus(taskId, UpdateStatusRequest(status))
    }

    override suspend fun updateNote(taskId: Long, note: String): TaskSyncResponse {
        return taskApiService.updateNote(taskId, UpdateNoteRequest(note))
    }

    override suspend fun updateTask(taskId: Long, task: TaskDetailsNetwork): TaskSyncResponse {
        return taskApiService.updateTask(taskId, task)
    }

    override suspend fun deleteTask(taskId: Long): TaskSyncResponse {
        return taskApiService.deleteTask(taskId)
    }

    override suspend fun createTask(task: TaskDetailsNetwork): TaskSyncResponse {
        return taskApiService.createTask(task)
    }
}