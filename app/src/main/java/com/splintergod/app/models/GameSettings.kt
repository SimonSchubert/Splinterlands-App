package com.splintergod.app.models

import com.google.gson.*
import com.google.gson.annotations.*

data class GameSettings(
    @SerializedName("asset_url") val assetUrl: String = "",
    val season: SeasonSettings = SeasonSettings(),
    val battles: BattleSettings = BattleSettings(),
    @SerializedName("daily_quests") val dailyQuests: List<Focus> = emptyList()
)