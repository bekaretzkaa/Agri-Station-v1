package com.example.agristation1.network.sensorNetwork

import javax.inject.Inject

interface NetworkSensorRepository {

    suspend fun getSensors(): SensorNetwork

}

class NetworkSensorRepositoryImpl @Inject constructor(
    private val sensorApiService: SensorApiService
) : NetworkSensorRepository {

    override suspend fun getSensors(): SensorNetwork {
        return sensorApiService.getSensors()
    }

}