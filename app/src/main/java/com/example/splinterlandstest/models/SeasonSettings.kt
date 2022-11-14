package com.example.splinterlandstest.models

import com.example.splinterlandstest.simpleDateFormat

data class SeasonSettings(val ends: String = "") {

    fun getEndTimestamp(): Long {
        return (simpleDateFormat.parse(ends)?.time?.div(1_000)
            ?: 0L)
    }
}