package com.example.agristation1.data.fieldDetails

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDetailsDao {
    @Query("SELECT * FROM field_details WHERE lifecycle !=  3 ORDER BY lifecycle ASC")
    fun getAllFields(): Flow<List<FieldDetails>>

    @Query("SELECT * FROM field_details")
    suspend fun getAllFieldsList(): List<FieldDetails>

    @Query("SELECT * FROM field_details WHERE lifecycle = 3")
    fun getArchivedFields(): Flow<List<FieldDetails>>

    @Query("SELECT * FROM field_details WHERE id = :id")
    fun getFieldByIdStream(id: Long): Flow<FieldDetails?>

    @Query("SELECT * FROM field_details WHERE id = :id")
    fun getFieldById(id: Long): FieldDetails?

    @Query("SELECT * FROM field_details WHERE health = 1 OR health = 2")
    fun getImmediateAttentionFields(): Flow<List<FieldDetails>>

    @Upsert
    suspend fun upsert(fieldDetails: FieldDetails)

    @Query("""
        UPDATE field_details
        SET
            last_valid_soil_moisture = COALESCE(soil_moisture, last_valid_soil_moisture),
            last_valid_soil_temperature = COALESCE(soil_temperature, last_valid_soil_temperature),
            last_valid_air_temperature = COALESCE(air_temperature, last_valid_air_temperature),
            last_valid_air_humidity = COALESCE(air_humidity, last_valid_air_humidity),
            last_valid_lux = COALESCE(lux, last_valid_lux)
        WHERE
            soil_moisture IS NOT NULL OR
            soil_temperature IS NOT NULL OR
            air_temperature IS NOT NULL OR
            air_humidity IS NOT NULL OR
            lux IS NOT NULL
    """)
    suspend fun copyCurrentValuesToLastValid()

    @Transaction
    @Query("SELECT * FROM field_details WHERE id = :id")
    suspend fun getFieldWithPointsById(id: Long): FieldWithPoints

    @Upsert
    suspend fun upsertFieldPoints(fieldPoints: List<FieldPoints>)
}