package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDetails(
    val capture_rate: Int,
    val rank: String,
    val rating: Int,
    val modern_rating: Int,
    val wins: Int,
    val name: String
)