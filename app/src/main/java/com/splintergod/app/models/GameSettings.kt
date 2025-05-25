package com.splintergod.app.models

import com.google.gson.annotations.SerializedName

data class GameSettings(
    @SerializedName("asset_url") val assetUrl: String = "",
    val season: SeasonSettings = SeasonSettings(),
    val battles: BattleSettings = BattleSettings(),
    @SerializedName("daily_quests") val dailyQuests: List<Focus> = emptyList(),
    @SerializedName("last_block") val lastBlock: Long = 0L
)