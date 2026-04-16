package com.example.agristation1.data

import android.text.format.DateUtils
import java.time.Instant


fun formatRelativeTime(instant: Instant?): String {
    return if(instant == null) {
        ""
    } else {
        DateUtils.getRelativeTimeSpanString(
            instant.toEpochMilli(),
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }
}
