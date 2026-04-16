package com.example.agristation1.network.userNetwork

import kotlinx.serialization.Serializable

@Serializable
data class FarmDetailsNetwork(
    val id: Long,
    val farmName: String,
    val activeSensors: Int,
    val totalSensors: Int
)
