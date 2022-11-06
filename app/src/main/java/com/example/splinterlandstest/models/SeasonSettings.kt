package com.example.splinterlandstest.models

import com.example.splinterlandstest.simpleDateFormat
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

@Serializable
data class SeasonSettings(val ends: String = "") {
    fun getFormattedEndDate(): String {
        if (ends.isEmpty()) {
            return ""
        }
        val milliseconds = (simpleDateFormat.parse(ends)?.time
            ?: 0L) - System.currentTimeMillis()
        return if (milliseconds <= 0) {
            "Claim reward"
        } else {
            "${milliseconds.div(1000L).seconds}"
        }
    }

    fun getEndTimestamp(): Long {
        return (simpleDateFormat.parse(ends)?.time?.div(1_000)
            ?: 0L)
    }
}