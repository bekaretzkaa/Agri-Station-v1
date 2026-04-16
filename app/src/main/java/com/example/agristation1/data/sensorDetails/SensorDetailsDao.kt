package com.example.agristation1.data.sensorDetails

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDetailsDao {
    @Query("SELECT * FROM sensor_details WHERE field_id = :fieldId")
    fun getSensorDetailsByFieldId(fieldId: Long): Flow<List<SensorDetails>>

    @Upsert
    suspend fun upsertSensors(sensorDetails: List<SensorDetails>)
}