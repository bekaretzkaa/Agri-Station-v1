package com.example.agristation1.data.taskNotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agristation1.data.taskDetails.TaskDetails
import java.time.Instant

@Entity(
    tableName = "task_notes",
    foreignKeys = [
        ForeignKey(
            entity = TaskDetails::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["task_id"], name = "idx_task_notes_task_id")
    ]
)
data class TaskNotes(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Int,

    val note: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant
)