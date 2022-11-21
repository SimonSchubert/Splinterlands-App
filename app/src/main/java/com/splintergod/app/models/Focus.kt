package com.splintergod.app.models

import com.google.gson.annotations.SerializedName

data class Focus(
    val name: String,
    @SerializedName("min_rating") val minRating: Int,
    val data: FocusData
)

data class FocusData(
    val description: String,
    val splinter: String? = null,
    val abilities: List<String>? = emptyList()
)