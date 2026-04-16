package com.example.agristation1.network.sensorNetwork

import kotlinx.serialization.Serializable

@Serializable
data class SensorNetwork(
    val sensors: List<SensorDetailsNetwork>? = null
)

@Serializable
data class SensorDetailsNetwork(
    val id: Long,
    val fieldId: Long,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val battery: Int,
    val state: Int
)