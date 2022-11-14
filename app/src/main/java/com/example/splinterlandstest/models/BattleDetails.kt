package com.example.splinterlandstest.models

import com.google.gson.annotations.SerializedName


data class BattleDetails(
    val team1: BattleDetailsTeam?,
    val team2: BattleDetailsTeam?,
    @SerializedName("is_brawl") val isBrawl: Boolean = false
)