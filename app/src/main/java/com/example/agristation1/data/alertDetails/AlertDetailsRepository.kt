package com.example.agristation1.data.alertDetails

import kotlinx.coroutines.flow.Flow

interface AlertDetailsRepository {
    fun getAllAlertsStream(): Flow<List<AlertDetails>>

    fun getResolvedAlertsStream(): Flow<List<AlertDetails>>

    fun getDismissedAlertsStream(): Flow<List<AlertDetails>>

    fun getAlertsByFieldIdStream(fieldId: Int): Flow<List<AlertDetails>>

    fun getAlertByIdStream(alertId: Int): Flow<AlertDetails?>

    suspend fun markAlertAsAcknowledged(alertId: Int)

    suspend fun markAlertAsUnAcknowledged(alertId: Int)

    suspend fun markAlertAsResolved(alertId: Int)

    suspend fun markAlertAsDismissed(alertId: Int)

    suspend fun deleteAlert(alertId: Int)
}

class AlertDetailsOfflineRepository(
    private val alertDetailsDao: AlertDetailsDao
) : AlertDetailsRepository {

    override fun getAllAlertsStream(): Flow<List<AlertDetails>> {
        return alertDetailsDao.getAllAlerts()
    }

    override fun getResolvedAlertsStream(): Flow<List<AlertDetails>> {
        return alertDetailsDao.getResolvedAlerts()
    }

    override fun getDismissedAlertsStream(): Flow<List<AlertDetails>> {
        return alertDetailsDao.getDismissedAlerts()
    }

    override fun getAlertsByFieldIdStream(fieldId: Int): Flow<List<AlertDetails>> {
        return alertDetailsDao.getAlertsByFieldId(fieldId)
    }

    override fun getAlertByIdStream(alertId: Int): Flow<AlertDetails?> {
        return alertDetailsDao.getAlertById(alertId)
    }

    override suspend fun markAlertAsAcknowledged(alertId: Int) {
        return alertDetailsDao.markAlertAsAcknowledged(alertId)
    }

    override suspend fun markAlertAsUnAcknowledged(alertId: Int) {
        return alertDetailsDao.markAlertAsUnAcknowledged(alertId)
    }

    override suspend fun markAlertAsResolved(alertId: Int) {
        return alertDetailsDao.markAlertAsResolved(alertId)
    }

    override suspend fun markAlertAsDismissed(alertId: Int) {
        return alertDetailsDao.markAlertAsDismissed(alertId)
    }

    override suspend fun deleteAlert(alertId: Int) {
        alertDetailsDao.deleteAlert(alertId)
    }

}