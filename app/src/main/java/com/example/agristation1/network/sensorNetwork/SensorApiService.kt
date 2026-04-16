package com.example.agristation1.network.sensorNetwork

import retrofit2.http.GET

interface SensorApiService {

    @GET("sensors")
    suspend fun getSensors(): SensorNetwork
}