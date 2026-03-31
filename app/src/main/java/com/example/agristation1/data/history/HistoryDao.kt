package com.example.agristation1.data.history

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface HistoryDao {

    @Query("""SELECT * FROM soil_moisture_history WHERE field_id = :fieldId
        AND recorded_at >= :start ORDER BY recorded_at ASC""")
    suspend fun getSoilMoistureByFieldIdInRange(
        fieldId: Int,
        start: Instant
    ): List<SoilMoisture>

    @Query("""SELECT * FROM soil_temperature_history WHERE field_id = :fieldId
        AND recorded_at >= :start ORDER BY recorded_at ASC""")
    suspend fun getSoilTemperatureByFieldIdInRange(
        fieldId: Int,
        start: Instant
    ): List<SoilTemperature>

    @Query("""SELECT * FROM air_temperature_history WHERE field_id = :fieldId
        AND recorded_at >= :start ORDER BY recorded_at ASC""")
    suspend fun getAirTemperatureByFieldIdInRange(
        fieldId: Int,
        start: Instant
    ): List<AirTemperature>

    @Query("""SELECT * FROM air_humidity_history WHERE field_id = :fieldId
        AND recorded_at >= :start ORDER BY recorded_at ASC""")
    suspend fun getAirHumidityByFieldIdInRange(
        fieldId: Int,
        start: Instant
    ): List<AirHumidity>

    @Query("""SELECT * FROM lux_history WHERE field_id = :fieldId
        AND recorded_at >= :start ORDER BY recorded_at ASC""")
    suspend fun getLuxByFieldIdInRange(
        fieldId: Int,
        start: Instant
    ): List<Lux>
}