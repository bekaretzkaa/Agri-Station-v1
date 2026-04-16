package com.example.agristation1.data.alertDetails

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

interface AlertDetailsRepository {
    fun getAllAlertsStream(): Flow<List<AlertDetails>>

    fun getAlertsByFieldIdStream(fieldId: Long): Flow<List<AlertDetails>>

    fun getAlertByIdStream(alertId: Long): Flow<AlertDetails?>

    suspend fun deleteAlert(alertId: Long)

    fun getImmediateAttentionAlertsStream(): Flow<List<AlertDetails>>
}

class AlertDetailsOfflineRepository(
    private val alertDetailsDao: AlertDetailsDao
) : AlertDetailsRepository {

    override fun getAllAlertsStream(): Flow<List<AlertDetails>> {
        return alertDetailsDao.getAllAlerts()
    }

    override fun getAlertsByFieldIdStream(fieldId: Long): Flow<List<AlertDetails>> {
        return alertDetailsDao.getAlertsByFieldId(fieldId)
    }

    override fun getAlertByIdStream(alertId: Long): Flow<AlertDetails?> {
        return alertDetailsDao.getAlertById(alertId)
    }

    override suspend fun deleteAlert(alertId: Long) {
        alertDetailsDao.deleteAlert(alertId)
    }

    suspend fun getAllAlertsList(): List<AlertDetails> {
        return alertDetailsDao.getAlertsList()
    }

    suspend fun upsertAlert(alert: AlertDetails) {
        alertDetailsDao.upsert(alert)
    }

    suspend fun updateLifecycle(alertId: Long, lifecycle: AlertLifecycle) {
        alertDetailsDao.updateLifecycle(alertId, lifecycle.code)
    }

    override fun getImmediateAttentionAlertsStream(): Flow<List<AlertDetails>> {
        return alertDetailsDao.getImmediateAttentionAlerts()
    }
}