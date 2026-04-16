package com.example.agristation1.data.sensorDetails

import kotlinx.coroutines.flow.Flow

interface SensorDetailsRepository {

    fun getSensorDetailsByFieldId(fieldId: Long): Flow<List<SensorDetails>>

    suspend fun upsertSensors(sensorDetails: List<SensorDetails>)

}

class SensorDetailsOfflineRepository(
    private val sensorDetailsDao: SensorDetailsDao
) : SensorDetailsRepository {

    override fun getSensorDetailsByFieldId(fieldId: Long): Flow<List<SensorDetails>> {
        return sensorDetailsDao.getSensorDetailsByFieldId(fieldId)
    }

    override suspend fun upsertSensors(sensorDetails: List<SensorDetails>) {
        sensorDetailsDao.upsertSensors(sensorDetails)
    }

}