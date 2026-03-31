package com.example.agristation1.fakedata

import com.example.agristation1.data.taskNotes.TaskNotes

import java.time.Instant
import java.time.temporal.ChronoUnit

object FakeTaskNotesData {

    private val now = Instant.now()

    val taskNotes = listOf(
        TaskNotes(
            taskId = 1,
            note = "Pump pressure checked before irrigation start.",
            createdAt = Instant.now().minus(4, ChronoUnit.HOURS)
        ),
        TaskNotes(
            taskId = 1,
            note = "Water flow stable after first 5 minutes.",
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS)
        ),
        TaskNotes(
            taskId = 2,
            note = "One sensor battery was below 10%.",
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS)
        ),
        TaskNotes(
            taskId = 2,
            note = "Gateway restarted successfully.",
            createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        TaskNotes(
            taskId = 3,
            note = "Vent windows opened manually.",
            createdAt = Instant.now().minus(90, ChronoUnit.MINUTES)
        ),
        TaskNotes(
            taskId = 4,
            note = "Shade screen deployed to 60% coverage.",
            createdAt = Instant.now().minus(1, ChronoUnit.HOURS)
        ),
        TaskNotes(
            taskId = 6,
            note = "Emergency irrigation approved by operator.",
            createdAt = Instant.now().minus(70, ChronoUnit.MINUTES)
        ),
        TaskNotes(
            taskId = 7,
            note = "Sensor may need recalibration tomorrow morning.",
            createdAt = Instant.now().minus(5, ChronoUnit.HOURS)
        )
    )
}