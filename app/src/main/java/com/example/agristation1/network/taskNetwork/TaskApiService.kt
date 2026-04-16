package com.example.agristation1.network.taskNetwork

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApiService {
    @GET("tasks")
    suspend fun getTasks(): TasksNetwork

    @PATCH("tasks/{id}/status")
    suspend fun updateStatus(
        @Path("id") taskId: Long,
        @Body request: UpdateStatusRequest
    ): TaskSyncResponse

    @PATCH("tasks/{id}/note")
    suspend fun updateNote(
        @Path("id") taskId: Long,
        @Body request: UpdateNoteRequest
    ): TaskSyncResponse

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
        @Body request: TaskDetailsNetwork
    ): TaskSyncResponse

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long
    ): TaskSyncResponse

    @POST("tasks")
    suspend fun createTask(
        @Body request: TaskDetailsNetwork
    ): TaskSyncResponse
}

@Serializable
data class TaskSyncResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class UpdateStatusRequest(
    val status: Int
)

@Serializable
data class UpdateNoteRequest(
    val note: String
)