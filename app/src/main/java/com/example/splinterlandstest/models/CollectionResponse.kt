package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponse(val player: String, val cards: List<Card>)