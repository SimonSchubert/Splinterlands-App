package com.example.splinterlandstest.models

import com.google.gson.annotations.SerializedName

data class PlayerDetails(
    @SerializedName("capture_rate") val captureRate: Int,
    val rank: String,
    val rating: Int,
    @SerializedName("modern_rating") val modernRating: Int,
    val wins: Int,
    val name: String
)