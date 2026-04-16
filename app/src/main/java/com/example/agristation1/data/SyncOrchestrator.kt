package com.example.agristation1.data

import com.example.agristation1.network.alertNetwork.AlertSyncManager
import com.example.agristation1.network.fieldNetwork.FieldSyncManager
import com.example.agristation1.network.sensorNetwork.SensorSyncManager
import com.example.agristation1.network.taskNetwork.TaskSyncManager
import com.example.agristation1.network.userNetwork.UserFarmSyncManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

sealed class SyncResult {
    object Success : SyncResult()
    data class PartialSuccess(val failedOps: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
}

class SyncOrchestrator(
    private val fieldSyncManager: FieldSyncManager,
    private val alertSyncManager: AlertSyncManager,
    private val taskSyncManager: TaskSyncManager,
    private val userFarmSyncManager: UserFarmSyncManager,
    private val sensorSyncManager: SensorSyncManager
) {
    suspend fun syncAll(since: Long): SyncResult {
        return try {
            val results = mutableListOf<SyncResult>()

            results += userFarmSyncManager.sync()
            results += fieldSyncManager.sync(since)

            val level2 = coroutineScope {
                awaitAll(
                    async { alertSyncManager.sync() },
                    async { sensorSyncManager.sync() }
                )
            }
            results += level2

            results += taskSyncManager.sync()

            mergeResults(results)
        } catch (e: Exception) {
            SyncResult.Error("Sync failed: ${e.message}")
        }
    }

    private fun mergeResults(results: List<SyncResult>): SyncResult {
        val errors = results.filterIsInstance<SyncResult.Error>()
        if (errors.isNotEmpty()) return errors.first()

        val totalFailed = results
            .filterIsInstance<SyncResult.PartialSuccess>()
            .sumOf { it.failedOps }

        return if(totalFailed == 0) SyncResult.Success
        else SyncResult.PartialSuccess(totalFailed)
    }
}