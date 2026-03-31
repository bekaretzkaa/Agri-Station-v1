package com.example.agristation1.data

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


fun formatRelativeTime(instant: Instant): String {
    return DateUtils.getRelativeTimeSpanString(
        instant.toEpochMilli(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

fun Instant.toDd(zoneId: ZoneId = ZoneId.systemDefault()): String {
    val formatter = DateTimeFormatter.ofPattern("dd")
    return atZone(zoneId).format(formatter)
}

fun Instant.toDdInt(zoneId: ZoneId = ZoneId.systemDefault()): Int {
    val formatter = DateTimeFormatter.ofPattern("dd")
    return atZone(zoneId).format(formatter).toInt()
}

fun LocalDate.toUiDueDate(
    today: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault()
): String {
    val weekFields = WeekFields.of(locale)

    val isSameWeek =
        this.get(weekFields.weekOfWeekBasedYear()) == today.get(weekFields.weekOfWeekBasedYear()) &&
                this.get(weekFields.weekBasedYear()) == today.get(weekFields.weekBasedYear())

    return when {
        this == today -> "Today"
        this == today.plusDays(1) -> "Tomorrow"
        this == today.minusDays(1) -> "Yesterday"

        isSameWeek -> {
            this.format(DateTimeFormatter.ofPattern("EEEE", locale))
        }

        this.year == today.year -> {
            this.format(DateTimeFormatter.ofPattern("d MMM", locale))
        }

        else -> {
            this.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
        }
    }
}