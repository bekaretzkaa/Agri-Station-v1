package com.example.agristation1.data.alertDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.fieldDetails.FieldDetails
import java.time.Instant

@Entity(
    tableName = "alert_details",
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
        Index(value = ["field_id"], name = "idx_alert_details_field_id")
    ]
)
data class AlertDetails(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "field_id")
    val fieldId: Int,
    val title: String?,
    val description: String?,
    val recommendation: String?,
    @ColumnInfo(name = "current_value")
    val currentValue: String?,
    val unit: String?,
    val threshold: String?,
    @ColumnInfo(name = "expected_range")
    val expectedRange: String?,
    val deviation: String?,
    @ColumnInfo(name = "sensor_id")
    val sensorId: String?,
    @ColumnInfo(name = "detected_at")
    val detectedAt: Instant,

    val lifecycle: AlertLifecycle,
    val severity: AlertSeverity,
    val verification: AlertVerification,
    val type: AlertType,
)