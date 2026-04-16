package com.example.agristation1.data.fieldDetails

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.agristation1.data.farmDetails.FarmDetails
import com.google.android.gms.maps.model.LatLng

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
    val id: Long,
    @ColumnInfo(name = "farm_id")
    val farmId: Long,

    val title: String?,
    val area: Double?,
    val type: String?,

    @ColumnInfo(name = "soil_moisture")
    val soilMoisture: Double?,
    @ColumnInfo(name = "last_valid_soil_moisture")
    val lastValidSoilMoisture: Double?,
    @ColumnInfo(name = "soil_temperature")
    val soilTemperature: Double?,
    @ColumnInfo(name = "last_valid_soil_temperature")
    val lastValidSoilTemperature: Double?,
    @ColumnInfo(name = "air_temperature")
    val airTemperature: Double?,
    @ColumnInfo(name = "last_valid_air_temperature")
    val lastValidAirTemperature: Double?,
    @ColumnInfo(name = "air_humidity")
    val airHumidity: Double?,
    @ColumnInfo(name = "last_valid_air_humidity")
    val lastValidAirHumidity: Double?,
    val lux: Double?,
    @ColumnInfo(name = "last_valid_lux")
    val lastValidLux: Double?,

    @ColumnInfo(name = "total_sensors")
    val totalSensors: Int?,
    @ColumnInfo(name = "active_sensors")
    val activeSensors: Int?,

    val health: FieldHealth,
    val connectivity: FieldConnectivity,
    val lifecycle: FieldLifecycle
)

@Entity(
    tableName = "field_points",
    foreignKeys = [
        ForeignKey(
            entity = FieldDetails::class,
            parentColumns = ["id"],
            childColumns = ["field_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["field_id"], name = "idx_field_points_field_id")
    ]
)
data class FieldPoints(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "field_id")
    val fieldId: Long,
    @ColumnInfo(name = "point_order")
    val pointOrder: Int,
    val latitude: Double,
    val longitude: Double
)

data class FieldWithPoints(
    @Embedded val field: FieldDetails,
    @Relation(
        parentColumn = "id",
        entityColumn = "field_id"
    )
    val points: List<FieldPoints>
)

fun FieldWithPoints.toLatLngList(): List<LatLng> {
    return points
        .sortedBy { it.pointOrder }
        .map { LatLng(it.latitude, it.longitude) }
}

fun List<LatLng>.center(): LatLng {
    val lat = sumOf { it.latitude } / size
    val lng = sumOf { it.longitude } / size
    return LatLng(lat, lng)
}