package com.example.agristation1.data.history

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM soil_moisture_history WHERE field_id = :fieldId ORDER BY recorded_at DESC")
    fun getSoilMoistureByFieldId(fieldId: Int): Flow<List<SoilMoisture>>

    @Query("SELECT * FROM soil_temperature_history WHERE field_id = :fieldId ORDER BY recorded_at DESC")
    fun getSoilTemperatureByFieldId(fieldId: Int): Flow<List<SoilTemperature>>

    @Query("SELECT * FROM air_temperature_history WHERE field_id = :fieldId ORDER BY recorded_at DESC")
    fun getAirTemperatureByFieldId(fieldId: Int): Flow<List<AirTemperature>>

    @Query("SELECT * FROM air_humidity_history WHERE field_id = :fieldId ORDER BY recorded_at DESC")
    fun getAirHumidityByFieldId(fieldId: Int): Flow<List<AirHumidity>>

    @Query("SELECT * FROM lux_history WHERE field_id = :fieldId ORDER BY recorded_at DESC")
    fun getLuxByFieldId(fieldId: Int): Flow<List<Lux>>

}