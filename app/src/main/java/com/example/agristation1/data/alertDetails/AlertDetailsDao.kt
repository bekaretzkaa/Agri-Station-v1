package com.example.agristation1.data.alertDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDetailsDao {
    @Query("SELECT * FROM alert_details ORDER BY detected_at DESC")
    fun getAllAlerts(): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE field_id = :fieldId")
    fun getAlertsByFieldId(fieldId: Long): Flow<List<AlertDetails>>

    @Query("SELECT * FROM alert_details WHERE id = :alertId")
    fun getAlertById(alertId: Long): Flow<AlertDetails?>

    @Query("DELETE FROM alert_details WHERE id = :alertId")
    suspend fun deleteAlert(alertId: Long)

    @Query("SELECT * FROM alert_details")
    suspend fun getAlertsList(): List<AlertDetails>

    @Upsert
    suspend fun upsert(alert: AlertDetails)

    @Query("UPDATE alert_details SET lifecycle = :lifecycle WHERE id = :alertId")
    suspend fun updateLifecycle(alertId: Long, lifecycle: Int)

    @Query("SELECT * FROM alert_details WHERE severity = 2 OR severity = 3")
    fun getImmediateAttentionAlerts(): Flow<List<AlertDetails>>
}