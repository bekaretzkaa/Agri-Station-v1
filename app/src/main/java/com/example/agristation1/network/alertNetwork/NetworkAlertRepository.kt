package com.example.agristation1.network.alertNetwork

import retrofit2.HttpException
import kotlin.math.sin

interface NetworkAlertRepository {
    suspend fun getAlerts(): AlertsNetwork
    suspend fun updateLifecycle(alertId: Long, lifecycle: Int): AlertSyncResponse
    suspend fun deleteAlert(alertId: Long): AlertSyncResponse
}

class NetworkAlertRepositoryImpl(
    private val alertApiService: AlertApiService
) : NetworkAlertRepository {

    override suspend fun getAlerts(): AlertsNetwork {
        return alertApiService.getAlerts()
    }

    override suspend fun updateLifecycle(alertId: Long, lifecycle: Int): AlertSyncResponse {
        return alertApiService.updateLifecycle(alertId, UpdateLifecycleRequest(lifecycle))
    }

    override suspend fun deleteAlert(alertId: Long): AlertSyncResponse {
        return try {
            alertApiService.deleteAlert(alertId)
        } catch (e: HttpException) {
            if(e.code() == 404) AlertSyncResponse(success = true)
            else throw e
        }
    }

}