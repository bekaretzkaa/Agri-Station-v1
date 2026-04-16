package com.example.agristation1.network.alertNetwork

import kotlinx.serialization.Serializable

@Serializable
data class AlertsNetwork(
    val alerts: List<AlertDetailsNetwork>? = null
)

@Serializable
data class AlertDetailsNetwork(
    val id: Long?,
    val fieldId: Long?,
    val title: String?,
    val description: String?,
    val recommendation: String?,
    val currentValue: String?,
    val unit: String?,
    val threshold: String?,
    val expectedRange: String?,
    val deviation: String?,
    val sensorId: String?,
    val detectedAt: Long?,
    val lifecycle: Int,
    val severity: Int,
    val verification: Int,
    val type: Int,
)