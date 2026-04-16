package com.example.agristation1.network.alertNetwork

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface AlertApiService {
    @GET("alerts")
    suspend fun getAlerts(): AlertsNetwork

    @PATCH("alerts/{id}/lifecycle")
    suspend fun updateLifecycle(
        @Path("id") alertId: Long,
        @Body request: UpdateLifecycleRequest
    ): AlertSyncResponse

    @DELETE("alerts/{id}")
    suspend fun deleteAlert(
        @Path("id") alertId: Long
    ): AlertSyncResponse
}

@Serializable
data class UpdateLifecycleRequest(
    val lifecycle: Int
)

@Serializable
data class AlertSyncResponse(
    val success: Boolean,
    val message: String? = null
)