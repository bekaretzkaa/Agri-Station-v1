package com.example.agristation1.network.taskNetwork

import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TasksNetwork(
    val tasks: List<TaskDetailsNetwork>? = null
)

@Serializable
data class TaskDetailsNetwork(
    val id: Long?,

    val title: String?,
    val description: String?,
    val notes: String?,

    val fieldId: Long,
    val alertId: Long?,

    val timeDue: Long?,
    val timeCreated: Long,

    val status: Int,
    val priority: Int,
    val type: Int,

    val alertDeleted: Boolean?
)

fun TaskDetailsNetwork.toEntity(): TaskDetails {
    return TaskDetails(
        id = id ?: 0,
        title = title,
        description = description,
        notes = notes,
        fieldId = fieldId,
        alertId = alertId,
        timeDue = if(timeDue != null) Instant.ofEpochMilli(timeDue) else null,
        timeCreated = Instant.ofEpochMilli(timeCreated),
        status = TaskStatus.fromCode(status),
        priority = TaskPriority.fromCode(priority),
        type = TaskType.fromCode(type),
        alertDeleted = alertDeleted ?: false
    )
}

fun TaskDetails.toNetwork(): TaskDetailsNetwork {
    return TaskDetailsNetwork(
        id = if (id == 0L) null else id,
        title = title,
        description = description,
        notes = notes,
        fieldId = fieldId,
        alertId = alertId,
        timeDue = timeDue?.toEpochMilli(),
        timeCreated = timeCreated.toEpochMilli(),
        status = status.code,
        priority = priority.code,
        type = type.code,
        alertDeleted = alertDeleted
    )
}
