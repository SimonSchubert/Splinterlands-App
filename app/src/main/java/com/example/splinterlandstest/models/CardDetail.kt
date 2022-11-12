package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class CardDetail(
    val id: String,
    val name: String,
    val tier: Int? = -1,
    val rarity: Int,
    val color: String
)