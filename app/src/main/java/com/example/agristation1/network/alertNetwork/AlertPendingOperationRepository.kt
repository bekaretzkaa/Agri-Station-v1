package com.example.agristation1.network.alertNetwork

interface AlertPendingOperationRepository {
    suspend fun insert(operation: AlertPendingOperation)

    suspend fun getAllPending(): List<AlertPendingOperation>

    suspend fun getByAlertId(alertId: Long): List<AlertPendingOperation>

    suspend fun updateStatus(id: Long, status: AlertPendingOperationStatus)

    suspend fun markFailed(id: Long)

    suspend fun delete(id: Long)

    suspend fun hasPendingForAlert(alertId: Long): Boolean

    suspend fun deleteByAlertIdAndOperation(
        alertId: Long,
        operation: AlertPendingOperationType
    )
}

class AlertPendingOperationRepositoryImpl(
    private val dao: AlertPendingOperationDao
) : AlertPendingOperationRepository {

    override suspend fun insert(operation: AlertPendingOperation) {
        dao.insert(operation)
    }

    override suspend fun getAllPending(): List<AlertPendingOperation> {
        return dao.getAllPending()
    }

    override suspend fun getByAlertId(alertId: Long): List<AlertPendingOperation> {
        return dao.getByAlertId(alertId)
    }

    override suspend fun updateStatus(id: Long, status: AlertPendingOperationStatus) {
        dao.updateStatus(id, status)
    }

    override suspend fun markFailed(id: Long) {
        dao.markFailed(id)
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun hasPendingForAlert(alertId: Long): Boolean {
        return dao.hasPendingForAlert(alertId) > 0
    }

    override suspend fun deleteByAlertIdAndOperation(
        alertId: Long,
        operation: AlertPendingOperationType
    ) {
        dao.deleteByAlertIdAndOperation(alertId, operation)
    }
}