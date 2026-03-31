package com.example.agristation1.fakedata

import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType

import java.time.Instant
import java.time.LocalDate

object FakeTaskData {

    val tasks = listOf(
        TaskDetails(
            id = 4001,
            title = "Run irrigation on South Field",
            description = "Open irrigation line and raise soil moisture above 30%.",
            fieldId = 102,
            alertId = 3001,
            timeDue = LocalDate.parse("2026-03-28"),
            timeCreated = Instant.ofEpochSecond(1774598460),
            status = TaskStatus.IN_PROGRESS,
            priority = TaskPriority.HIGH,
            type = TaskType.SOIL_MOISTURE
        ),
        TaskDetails(
            id = 4002,
            title = "Inspect weak signal sensor",
            description = "Check antenna, gateway visibility and mounting.",
            fieldId = 102,
            alertId = 3002,
            timeDue = LocalDate.parse("2026-03-28"),
            timeCreated = Instant.ofEpochSecond(1774598760),
            status = TaskStatus.OPEN,
            priority = TaskPriority.MEDIUM,
            type = TaskType.SENSOR
        ),
        TaskDetails(
            id = 4003,
            title = "Heat stress mitigation",
            description = "Verify irrigation timing and inspect crop stress in South Field.",
            fieldId = 102,
            alertId = 3003,
            timeDue = LocalDate.parse("2026-03-28"),
            timeCreated = Instant.ofEpochSecond(1774599060),
            status = TaskStatus.OPEN,
            priority = TaskPriority.HIGH,
            type = TaskType.AIR_TEMPERATURE
        ),
        TaskDetails(
            id = 4004,
            title = "Check orchard humidity",
            description = "Review ventilation and inspect leaves for moisture-related issues.",
            fieldId = 103,
            alertId = 3004,
            timeDue = LocalDate.parse("2026-03-28"),
            timeCreated = Instant.ofEpochSecond(1774599360),
            status = TaskStatus.IN_PROGRESS,
            priority = TaskPriority.MEDIUM,
            type = TaskType.AIR_HUMIDITY
        ),
        TaskDetails(
            id = 4005,
            title = "Replace lux sensor in orchard",
            description = "Swap EO-LX-01 and verify readings after replacement.",
            fieldId = 103,
            alertId = 3008,
            timeDue = LocalDate.parse("2026-03-29"),
            timeCreated = Instant.ofEpochSecond(1774600560),
            status = TaskStatus.OPEN,
            priority = TaskPriority.HIGH,
            type = TaskType.SENSOR
        ),
        TaskDetails(
            id = 4006,
            title = "Review greenhouse heating setup",
            description = "Validate temperature controller profile and airflow settings.",
            fieldId = 104,
            alertId = 3006,
            timeDue = LocalDate.parse("2026-03-27"),
            timeCreated = Instant.ofEpochSecond(1774599960),
            status = TaskStatus.COMPLETED,
            priority = TaskPriority.MEDIUM,
            type = TaskType.SOIL_TEMPERATURE
        )
    )
}