package com.example.agristation1.network.sensorNetwork

interface NetworkSensorRepository {

    suspend fun getSensors(): SensorNetwork

}

class NetworkSensorRepositoryImpl(
    private val sensorApiService: SensorApiService
) : NetworkSensorRepository {

    override suspend fun getSensors(): SensorNetwork {
        return sensorApiService.getSensors()
    }

}