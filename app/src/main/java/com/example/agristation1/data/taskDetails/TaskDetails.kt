package com.example.agristation1.data.taskDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.fieldDetails.FieldDetails
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "task_details",
    foreignKeys = [
        ForeignKey(
            entity = FieldDetails::class,
            parentColumns = ["id"],
            childColumns = ["field_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.NO_ACTION
        ),
        ForeignKey(
            entity = AlertDetails::class,
            parentColumns = ["id"],
            childColumns = ["alert_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["field_id"], name = "idx_task_details_field_id"),
        Index(value = ["alert_id"], name = "idx_task_details_alert_id")
    ]
)
data class TaskDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String?,
    val description: String?,

    @ColumnInfo(name = "field_id")
    val fieldId: Int,
    @ColumnInfo(name = "alert_id")
    val alertId: Int?,

    @ColumnInfo(name = "time_due")
    val timeDue: LocalDate?,
    @ColumnInfo(name = "time_created")
    val timeCreated: Instant,

    val status: TaskStatus,
    val priority: TaskPriority,
    val type: TaskType
)