package com.example.agristation1.data.alertDetails

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDetailsDao {
    @Query("SELECT * FROM alert_details WHERE lifecycle NOT IN(2, 3) ORDER BY detected_at DESC")
    fun getAllAlerts(): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE lifecycle = 2 ORDER BY detected_at DESC")
    fun getResolvedAlerts(): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE lifecycle = 3 ORDER BY detected_at DESC")
    fun getDismissedAlerts(): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE field_id = :fieldId")
    fun getAlertsByFieldId(fieldId: Int): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE id = :alertId")
    fun getAlertById(alertId: Int): Flow<AlertDetails?>

    @Query("UPDATE alert_details SET lifecycle = 1 WHERE id = :alertId")
    suspend fun markAlertAsAcknowledged(alertId: Int)

    @Query("UPDATE alert_details SET lifecycle = 0 WHERE id = :alertId")
    suspend fun markAlertAsUnAcknowledged(alertId: Int)

    @Query("UPDATE alert_details SET lifecycle = 2 WHERE id = :alertId")
    suspend fun markAlertAsResolved(alertId: Int)

    @Query("UPDATE alert_details SET lifecycle = 3 WHERE id = :alertId")
    suspend fun markAlertAsDismissed(alertId: Int)

    @Query("DELETE FROM alert_details WHERE id = :alertId")
    suspend fun deleteAlert(alertId: Int)
}