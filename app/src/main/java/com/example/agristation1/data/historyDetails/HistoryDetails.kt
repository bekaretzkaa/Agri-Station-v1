package com.example.agristation1.data.historyDetails

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.fieldDetails.FieldDetails
import java.time.Instant


@Entity(
    tableName = "history_details",
    foreignKeys = [
        ForeignKey(
            entity = FieldDetails::class,
            parentColumns = ["id"],
            childColumns = ["field_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["field_id", "recorded_at"],
            unique = true,
            name = "idx_history_details_field_id_recorded_at"
        )
    ]
)
data class HistoryDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "field_id")
    val fieldId: Long,

    @ColumnInfo(name = "recorded_at")
    val recordedAt: Instant,

    @ColumnInfo(name = "soil_moisture")
    val soilMoisture: Double? = null,

    @ColumnInfo(name = "soil_temperature")
    val soilTemperature: Double? = null,

    @ColumnInfo(name = "air_temperature")
    val airTemperature: Double? = null,

    @ColumnInfo(name = "air_humidity")
    val airHumidity: Double? = null,

    val lux: Double? = null
)