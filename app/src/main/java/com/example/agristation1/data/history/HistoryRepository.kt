package com.example.agristation1.data.history

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getSoilMoistureByFieldIdStream(fieldId: Int): Flow<List<SoilMoisture>>

    fun getSoilTemperatureByFieldIdStream(fieldId: Int): Flow<List<SoilTemperature>>

    fun getAirTemperatureByFieldIdStream(fieldId: Int): Flow<List<AirTemperature>>

    fun getAirHumidityByFieldIdStream(fieldId: Int): Flow<List<AirHumidity>>

    fun getLuxByFieldIdStream(fieldId: Int): Flow<List<Lux>>

}

class HistoryOfflineRepository(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getSoilMoistureByFieldIdStream(fieldId: Int): Flow<List<SoilMoisture>> {
        return historyDao.getSoilMoistureByFieldId(fieldId)
    }

    override fun getSoilTemperatureByFieldIdStream(fieldId: Int): Flow<List<SoilTemperature>> {
        return historyDao.getSoilTemperatureByFieldId(fieldId)
    }

    override fun getAirTemperatureByFieldIdStream(fieldId: Int): Flow<List<AirTemperature>> {
        return historyDao.getAirTemperatureByFieldId(fieldId)
    }

    override fun getAirHumidityByFieldIdStream(fieldId: Int): Flow<List<AirHumidity>> {
        return historyDao.getAirHumidityByFieldId(fieldId)
    }

    override fun getLuxByFieldIdStream(fieldId: Int): Flow<List<Lux>> {
        return historyDao.getLuxByFieldId(fieldId)
    }

}