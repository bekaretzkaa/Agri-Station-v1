package com.example.agristation1.data.fieldDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.farmDetails.FarmDetails

@Entity(
    tableName = "field_details",
    foreignKeys = [
        ForeignKey(
            entity = FarmDetails::class,
            parentColumns = ["id"],
            childColumns = ["farm_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["farm_id"], name = "idx_field_details_farm_id")
    ]
    )
data class FieldDetails(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "farm_id")
    val farmId: Int,

    val title: String?,
    val area: Double?,
    val type: String?,

    @ColumnInfo(name = "soil_moisture")
    val soilMoisture: Int?,
    @ColumnInfo(name = "last_valid_soil_moisture")
    val lastValidSoilMoisture: Int?,
    @ColumnInfo(name = "soil_temperature")
    val soilTemperature: Int?,
    @ColumnInfo(name = "last_valid_soil_temperature")
    val lastValidSoilTemperature: Int?,
    @ColumnInfo(name = "air_temperature")
    val airTemperature: Int?,
    @ColumnInfo(name = "last_valid_air_temperature")
    val lastValidAirTemperature: Int?,
    @ColumnInfo(name = "air_humidity")
    val airHumidity: Int?,
    @ColumnInfo(name = "last_valid_air_humidity")
    val lastValidAirHumidity: Int?,
    val lux: Int?,
    @ColumnInfo(name = "last_valid_lux")
    val lastValidLux: Int?,

    @ColumnInfo(name = "total_sensors")
    val totalSensors: Int?,
    @ColumnInfo(name = "active_sensors")
    val activeSensors: Int?,

    val health: FieldHealth,
    val connectivity: FieldConnectivity,
    val lifecycle: FieldLifecycle
)