package com.example.agristation1.data.historyDetails

import kotlinx.coroutines.flow.Flow

interface HistoryOfflineRepository {
    // Temperature
    fun observeSoilTemperature(fieldId: Long, since: Long): Flow<List<Double>>
    fun observeSoilTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    fun observeAirTemperature(fieldId: Long, since: Long): Flow<List<Double>>
    fun observeAirTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    // Moisture
    fun observeSoilMoisture(fieldId: Long, since: Long): Flow<List<Double>>
    fun observeSoilMoistureAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    fun observeAirHumidity(fieldId: Long, since: Long): Flow<List<Double>>
    fun observeAirHumidityAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    // Lux
    fun observeLux(fieldId: Long, since: Long): Flow<List<Double>>
    fun observeLuxAggregated(fieldId: Long, since: Long, bucketSize: Long): Flow<List<Double>>

    // Insert
    suspend fun insertAll(history: List<HistoryDetails>)

    // Cleanup
    suspend fun deleteOlderThan(before: Long)
}

class HistoryOfflineRepositoryImpl(
    private val historyDetailsDao: HistoryDetailsDao
) : HistoryOfflineRepository {

    override fun observeSoilTemperature(fieldId: Long, since: Long) =
        historyDetailsDao.observeSoilTemperature(fieldId, since)

    override fun observeSoilTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long) =
        historyDetailsDao.observeSoilTemperatureAggregated(fieldId, since, bucketSize)

    override fun observeAirTemperature(fieldId: Long, since: Long) =
        historyDetailsDao.observeAirTemperature(fieldId, since)

    override fun observeAirTemperatureAggregated(fieldId: Long, since: Long, bucketSize: Long) =
        historyDetailsDao.observeAirTemperatureAggregated(fieldId, since, bucketSize)

    override fun observeSoilMoisture(fieldId: Long, since: Long) =
        historyDetailsDao.observeSoilMoisture(fieldId, since)

    override fun observeSoilMoistureAggregated(fieldId: Long, since: Long, bucketSize: Long) =
        historyDetailsDao.observeSoilMoistureAggregated(fieldId, since, bucketSize)

    override fun observeAirHumidity(fieldId: Long, since: Long) =
        historyDetailsDao.observeAirHumidity(fieldId, since)

    override fun observeAirHumidityAggregated(fieldId: Long, since: Long, bucketSize: Long) =
        historyDetailsDao.observeAirHumidityAggregated(fieldId, since, bucketSize)

    override fun observeLux(fieldId: Long, since: Long) =
        historyDetailsDao.observeLux(fieldId, since)

    override fun observeLuxAggregated(fieldId: Long, since: Long, bucketSize: Long) =
        historyDetailsDao.observeLuxAggregated(fieldId, since, bucketSize)

    override suspend fun insertAll(history: List<HistoryDetails>) =
        historyDetailsDao.insertAll(history)

    override suspend fun deleteOlderThan(before: Long) =
        historyDetailsDao.deleteOlderThan(before)
}