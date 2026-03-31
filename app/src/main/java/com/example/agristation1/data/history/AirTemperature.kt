package com.example.agristation1.data.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.fieldDetails.FieldDetails
import java.time.Instant

@Entity(
    tableName = "air_temperature_history",
    foreignKeys = [
        ForeignKey(
            entity = FieldDetails::class,
            parentColumns = ["id"],
            childColumns = ["field_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = ["field_id", "recorded_at"],
            name = "idx_air_temperature_history_field_time",
            orders = [Index.Order.ASC, Index.Order.DESC]
        )
    ]
)
data class AirTemperature(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "field_id")
    val fieldId: Int,

    val value: Int?,

    val status: HistoryStatus,

    @ColumnInfo(name = "recorded_at")
    val recordedAt: Instant
)