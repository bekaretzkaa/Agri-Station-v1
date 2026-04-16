package com.example.agristation1.network.fieldNetwork

import kotlinx.serialization.Serializable

@Serializable
data class FieldsSyncResponseDto(
    val serverTime: Long,
    val nextCursor: Long,
    val fields: List<FieldDto>,
    val fieldPoints: List<FieldPointDto>
)

@Serializable
data class FieldPointDto(
    val id: Long,
    val fieldId: Long,
    val pointOrder: Int,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class FieldDto(
    val id: Long,
    val farmId: Long,
    val title: String? = null,
    val area: Double? = null,
    val type: String? = null,
    val snapshot: FieldSnapshotDto,
    val history: List<FieldHistoryPointDto> = emptyList()
)

@Serializable
data class FieldSnapshotDto(
    val soilMoisture: Double? = null,
    val soilTemperature: Double? = null,
    val airTemperature: Double? = null,
    val airHumidity: Double? = null,
    val lux: Double? = null,
    val health: Int,
    val connectivity: Int,
    val lifecycle: Int,
    val activeSensors: Int,
    val totalSensors: Int
)

@Serializable
data class FieldHistoryPointDto(
    val recordedAt: Long,
    val soilMoisture: Double? = null,
    val soilTemperature: Double? = null,
    val airTemperature: Double? = null,
    val airHumidity: Double? = null,
    val lux: Double? = null
)