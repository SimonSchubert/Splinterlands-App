package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val asset_url: String = "",
    val season: SeasonSettings = SeasonSettings(),
    val battles: BattleSettings = BattleSettings(),
    val daily_quests: List<Focus> = emptyList()
)