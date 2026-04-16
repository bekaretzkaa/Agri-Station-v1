package com.example.agristation1.data.historyDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDetailsDao {
    // =========================
    // INSERT
    // =========================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(history: List<HistoryDetails>)


    // =========================
    // DAY
    // =========================

    @Query("SELECT soil_moisture FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND soil_moisture IS NOT NULL ORDER BY recorded_at ASC")
    fun observeSoilMoisture(fieldId: Long, since: Long): Flow<List<Double>>

    @Query("SELECT soil_temperature FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND soil_temperature IS NOT NULL ORDER BY recorded_at ASC")
    fun observeSoilTemperature(fieldId: Long, since: Long): Flow<List<Double>>

    @Query("SELECT air_temperature FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND air_temperature IS NOT NULL ORDER BY recorded_at ASC")
    fun observeAirTemperature(fieldId: Long, since: Long): Flow<List<Double>>

    @Query("SELECT air_humidity FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND air_humidity IS NOT NULL ORDER BY recorded_at ASC")
    fun observeAirHumidity(fieldId: Long, since: Long): Flow<List<Double>>

    @Query("SELECT lux FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND lux IS NOT NULL ORDER BY recorded_at ASC")
    fun observeLux(fieldId: Long, since: Long): Flow<List<Double>>


    // =========================
    // WEEK   OR    MONTH
    // =========================

    @Query("""
        SELECT AVG(soil_moisture) FROM history_details
        WHERE field_id = :fieldId AND recorded_at >= :since AND soil_moisture IS NOT NULL
        GROUP BY (recorded_at / :bucketSize)
        ORDER BY (recorded_at / :bucketSize) ASC
    """)
    fun observeSoilMoistureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    @Query("""
        SELECT AVG(soil_temperature) FROM history_details
        WHERE field_id = :fieldId AND recorded_at >= :since AND soil_temperature IS NOT NULL
        GROUP BY (recorded_at / :bucketSize)
        ORDER BY (recorded_at / :bucketSize) ASC
    """)
    fun observeSoilTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    @Query("""
        SELECT AVG(air_temperature) FROM history_details
        WHERE field_id = :fieldId AND recorded_at >= :since AND air_temperature IS NOT NULL
        GROUP BY (recorded_at / :bucketSize)
        ORDER BY (recorded_at / :bucketSize) ASC
    """)
    fun observeAirTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    @Query("""
        SELECT AVG(air_humidity) FROM history_details
        WHERE field_id = :fieldId AND recorded_at >= :since AND air_humidity IS NOT NULL
        GROUP BY (recorded_at / :bucketSize)
        ORDER BY (recorded_at / :bucketSize) ASC
    """)
    fun observeAirHumidityAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    @Query("""
        SELECT AVG(lux) FROM history_details
        WHERE field_id = :fieldId AND recorded_at >= :since AND lux IS NOT NULL
        GROUP BY (recorded_at / :bucketSize)
        ORDER BY (recorded_at / :bucketSize) ASC
    """)
    fun observeLuxAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>


    // =========================
    // MISSING STATISTICS
    // =========================

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND soil_moisture IS NULL")
    suspend fun countMissingSoilMoisture(fieldId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND soil_temperature IS NULL")
    suspend fun countMissingSoilTemperature(fieldId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND air_temperature IS NULL")
    suspend fun countMissingAirTemperature(fieldId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND air_humidity IS NULL")
    suspend fun countMissingAirHumidity(fieldId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since AND lux IS NULL")
    suspend fun countMissingLux(fieldId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM history_details WHERE field_id = :fieldId AND recorded_at >= :since")
    suspend fun countTotal(fieldId: Long, since: Long): Int


    // =========================
    // DELETE
    // =========================

    @Query("DELETE FROM history_details WHERE recorded_at < :before")
    suspend fun deleteOlderThan(before: Long)
}