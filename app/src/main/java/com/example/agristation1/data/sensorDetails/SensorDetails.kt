package com.example.agristation1.data.sensorDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.fieldDetails.FieldDetails

@Entity(
    tableName = "sensor_details",
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
        Index(value = ["field_id"], name = "idx_sensor_details_field_id")
    ]
)
data class SensorDetails (
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "field_id")
    val fieldId: Int,
    val name: String?,
    val coordinates: Int?,

    val battery: SensorBattery,
    val state: SensorState
)