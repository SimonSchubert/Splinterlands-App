package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class BattleDetailsTeam(val player: String, val summoner: Card, val monsters: List<Card>)