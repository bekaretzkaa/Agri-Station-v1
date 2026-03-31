package com.example.agristation1.data.history

import java.time.Instant

interface HistoryRepository {

    suspend fun getSoilMoistureLastDay(fieldId: Int): List<SoilMoisture>
    suspend fun getSoilMoistureLastWeek(fieldId: Int): List<SoilMoisture>
    suspend fun getSoilMoistureLastMonth(fieldId: Int): List<SoilMoisture>

    suspend fun getSoilTemperatureLastDay(fieldId: Int): List<SoilTemperature>
    suspend fun getSoilTemperatureLastWeek(fieldId: Int): List<SoilTemperature>
    suspend fun getSoilTemperatureLastMonth(fieldId: Int): List<SoilTemperature>

    suspend fun getAirTemperatureLastDay(fieldId: Int): List<AirTemperature>
    suspend fun getAirTemperatureLastWeek(fieldId: Int): List<AirTemperature>
    suspend fun getAirTemperatureLastMonth(fieldId: Int): List<AirTemperature>

    suspend fun getAirHumidityLastDay(fieldId: Int): List<AirHumidity>
    suspend fun getAirHumidityLastWeek(fieldId: Int): List<AirHumidity>
    suspend fun getAirHumidityLastMonth(fieldId: Int): List<AirHumidity>

    suspend fun getLuxLastDay(fieldId: Int): List<Lux>
    suspend fun getLuxLastWeek(fieldId: Int): List<Lux>
    suspend fun getLuxLastMonth(fieldId: Int): List<Lux>

}

class HistoryOfflineRepository(
    private val historyDao: HistoryDao
) : HistoryRepository {

    val day: Long = 24*60*60
    val week: Long = 7*24*60*60
    val month: Long = 30*24*60*60

    override suspend fun getSoilMoistureLastDay(fieldId: Int): List<SoilMoisture> {
        return historyDao.getSoilMoistureByFieldIdInRange(fieldId, Instant.now().minusSeconds(day))
    }

    override suspend fun getSoilMoistureLastWeek(fieldId: Int): List<SoilMoisture> {
        return historyDao.getSoilMoistureByFieldIdInRange(fieldId, Instant.now().minusSeconds(week))
    }

    override suspend fun getSoilMoistureLastMonth(fieldId: Int): List<SoilMoisture> {
        return historyDao.getSoilMoistureByFieldIdInRange(fieldId, Instant.now().minusSeconds(month))
    }

    override suspend fun getSoilTemperatureLastDay(fieldId: Int): List<SoilTemperature> {
        return historyDao.getSoilTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(day))
    }
    override suspend fun getSoilTemperatureLastWeek(fieldId: Int): List<SoilTemperature> {
        return historyDao.getSoilTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(week))
    }
    override suspend fun getSoilTemperatureLastMonth(fieldId: Int): List<SoilTemperature> {
        return historyDao.getSoilTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(month))
    }

    override suspend fun getAirTemperatureLastDay(fieldId: Int): List<AirTemperature> {
        return historyDao.getAirTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(day))
    }
    override suspend fun getAirTemperatureLastWeek(fieldId: Int): List<AirTemperature> {
        return historyDao.getAirTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(week))
    }
    override suspend fun getAirTemperatureLastMonth(fieldId: Int): List<AirTemperature> {
        return historyDao.getAirTemperatureByFieldIdInRange(fieldId, Instant.now().minusSeconds(month))
    }

    override suspend fun getAirHumidityLastDay(fieldId: Int): List<AirHumidity> {
        return historyDao.getAirHumidityByFieldIdInRange(fieldId, Instant.now().minusSeconds(day))
    }

    override suspend fun getAirHumidityLastWeek(fieldId: Int): List<AirHumidity> {
        return historyDao.getAirHumidityByFieldIdInRange(fieldId, Instant.now().minusSeconds(week))
    }

    override suspend fun getAirHumidityLastMonth(fieldId: Int): List<AirHumidity> {
        return historyDao.getAirHumidityByFieldIdInRange(fieldId, Instant.now().minusSeconds(month))
    }

    override suspend fun getLuxLastDay(fieldId: Int): List<Lux> {
        return historyDao.getLuxByFieldIdInRange(fieldId, Instant.now().minusSeconds(day))
    }
    override suspend fun getLuxLastWeek(fieldId: Int): List<Lux> {
        return historyDao.getLuxByFieldIdInRange(fieldId, Instant.now().minusSeconds(week))
    }
    override suspend fun getLuxLastMonth(fieldId: Int): List<Lux> {
        return historyDao.getLuxByFieldIdInRange(fieldId, Instant.now().minusSeconds(month))
    }
}