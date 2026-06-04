package com.example.agristation1.network.alertNetwork

import android.util.Log
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.alertDetails.AlertDetailsRepository
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.alertDetails.AlertType
import com.example.agristation1.data.alertDetails.AlertVerification
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsRepository
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.time.Instant
import javax.inject.Inject

interface AlertSyncManager {
    suspend fun sync(): SyncResult

    suspend fun pushPendingOperations(): Int

    suspend fun pushLifecycleUpdate(op: AlertPendingOperation): Boolean

    suspend fun pushDeleteAlert(op: AlertPendingOperation): Boolean

    suspend fun fetchAndMerge()

    suspend fun handleDisappearedAlerts(alertsIds: Set<Long>)

    suspend fun mergeAlert(
        remoteAlert: AlertDetailsNetwork,
        localAlerts: List<AlertDetails>
    )
}

class AlertSyncManagerImpl @Inject constructor(
    private val alertDetailsOfflineRepository: AlertDetailsRepository,
    private val taskDetailsOfflineRepository: TaskDetailsRepository,
    private val alertPendingOperationDao: AlertPendingOperationDao,
    private val networkAlertRepository: NetworkAlertRepository
): AlertSyncManager {

    override suspend fun sync(): SyncResult {
        return try {
            val failedOps = pushPendingOperations()

            Log.d("AlertSyncManager", "Failed ops: $failedOps")

            fetchAndMerge()

            if (failedOps == 0) SyncResult.Success
            else SyncResult.PartialSuccess(failedOps)
        } catch (e: IOException) {
            SyncResult.Error("No connection with server")
        } catch (e: HttpException) {
            SyncResult.Error("Server error: ${e.code()}")
        } catch (e: Exception) {
            SyncResult.Error("Unknown error: ${e.message}")
        }
    }


    override suspend fun pushPendingOperations(): Int {
        val pending = alertPendingOperationDao.getAllPending()
        var failedOps = 0

        for (op in pending) {

            Log.d("AlertSyncManager", "Pending ID: ${op.id}")

            alertPendingOperationDao.updateStatus(op.id, AlertPendingOperationStatus.IN_FLIGHT)

            val success = when (op.operation) {
                AlertPendingOperationType.UPDATE_LIFECYCLE -> pushLifecycleUpdate(op)
                AlertPendingOperationType.DELETE_ALERT -> pushDeleteAlert(op)
            }
            if (success) {
                alertPendingOperationDao.delete(op.id)
            } else {
                alertPendingOperationDao.markFailed(op.id)
                failedOps++
            }
        }

        return failedOps
    }

    override suspend fun pushLifecycleUpdate(op: AlertPendingOperation): Boolean {
        return try {
            val lifecycleCode = op.payload.toIntOrNull() ?: return false
            val result = networkAlertRepository.updateLifecycle(op.entityId, lifecycleCode)
            Log.d("AlertSyncManager", "UPDATE LIFECYCLE RESULT: $result")
            result.success
        } catch (e: Exception) {
            Log.d("AlertSyncManager", "ERROR UPDATE LIFECYCLE ${e.message}")
            false
        }
    }

    override suspend fun pushDeleteAlert(op: AlertPendingOperation): Boolean {
        return try {
            val result = networkAlertRepository.deleteAlert(op.entityId)
            Log.d("AlertSyncManager", "DELETE ALERT RESULT: $result")
            result.success
        } catch (e: Exception) {
            Log.d("AlertSyncManager", "ERROR DELETE ALERT ${e.message}")
            false
        }
    }


    override suspend fun fetchAndMerge() {
        val remoteAlerts = networkAlertRepository.getAlerts().alerts.orEmpty()
        val localAlerts = alertDetailsOfflineRepository.getAllAlertsList()

        val remoteIds = remoteAlerts.mapNotNull { it.id }.toSet()
        val localIds = localAlerts.map { it.id }.toSet()

        val disappearedAlerts = localIds - remoteIds
        handleDisappearedAlerts(disappearedAlerts)

        for (remoteAlert in remoteAlerts) {
            mergeAlert(remoteAlert, localAlerts)
        }
    }

    override suspend fun handleDisappearedAlerts(alertsIds: Set<Long>) {
        for (alertId in alertsIds) {
            val hasPending = alertPendingOperationDao.hasPendingForAlert(alertId) > 0
            if (hasPending) continue

            taskDetailsOfflineRepository.detachFromDeletedAlert(alertId)
            alertDetailsOfflineRepository.deleteAlert(alertId)
        }
    }

    override suspend fun mergeAlert(
        remoteAlert: AlertDetailsNetwork,
        localAlerts: List<AlertDetails>
    ) {
        val remoteId = remoteAlert.id ?: return
        val localAlert = localAlerts.find { it.id == remoteId }

        if (localAlert == null) {
            val pendingOperations = alertPendingOperationDao.getByAlertId(remoteId)
            if (pendingOperations.any { it.operation == AlertPendingOperationType.DELETE_ALERT }) return

            alertDetailsOfflineRepository.upsertAlert(remoteAlert.toEntity())
            return
        }

        val hasPending = alertPendingOperationDao.hasPendingForAlert(remoteId) > 0

        val merged = remoteAlert.toEntity().copy(
            lifecycle = if (hasPending)
                localAlert.lifecycle
            else
                AlertLifecycle.fromCode(remoteAlert.lifecycle)
        )

        alertDetailsOfflineRepository.upsertAlert(merged)
    }
}

fun AlertDetailsNetwork.toEntity(): AlertDetails {
    return AlertDetails(
        id = id ?: 0L,
        title = title,
        description = description,
        type = AlertType.fromCode(type),
        severity = AlertSeverity.fromCode(severity),
        lifecycle = AlertLifecycle.fromCode(lifecycle),
        fieldId = fieldId ?: 0L,
        recommendation = recommendation,
        currentValue = currentValue,
        unit = unit,
        threshold = threshold,
        expectedRange = expectedRange,
        deviation = deviation,
        sensorId = sensorId,
        detectedAt = Instant.ofEpochMilli(detectedAt ?: Instant.now().toEpochMilli()),
        verification = AlertVerification.fromCode(verification),
    )
}