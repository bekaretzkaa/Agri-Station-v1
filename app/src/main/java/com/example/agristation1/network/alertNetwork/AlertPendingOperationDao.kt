package com.example.agristation1.network.alertNetwork

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertPendingOperationDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(operation: AlertPendingOperation)

    @Query("SELECT * FROM alert_pending_operations WHERE status != 2 ORDER BY created_at ASC")
    suspend fun getAllPending(): List<AlertPendingOperation>

    @Query("SELECT * FROM alert_pending_operations WHERE entity_id = :alertId")
    suspend fun getByAlertId(alertId: Long): List<AlertPendingOperation>

    @Query("UPDATE alert_pending_operations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: AlertPendingOperationStatus)

    @Query("UPDATE alert_pending_operations SET status = 2 WHERE id = :id")
    suspend fun markFailed(id: Long)

    @Query("DELETE FROM alert_pending_operations WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM alert_pending_operations WHERE entity_id = :alertId AND status = 0")
    suspend fun hasPendingForAlert(alertId: Long): Int

    @Query("""
    DELETE FROM alert_pending_operations 
    WHERE entity_id = :alertId AND operation = :operation
    """)
    suspend fun deleteByAlertIdAndOperation(
        alertId: Long,
        operation: AlertPendingOperationType
    )
}