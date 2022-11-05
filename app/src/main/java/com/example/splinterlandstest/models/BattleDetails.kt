package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class BattleDetails(
    val team1: BattleDetailsTeam?,
    val team2: BattleDetailsTeam?,
    val is_brawl: Boolean = false
)