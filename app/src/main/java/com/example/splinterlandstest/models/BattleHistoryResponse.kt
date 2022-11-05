package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class BattleHistoryResponse(val player: String, val battles: List<Battle>)