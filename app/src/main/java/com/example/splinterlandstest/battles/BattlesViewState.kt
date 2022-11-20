package com.example.splinterlandstest.battles

import android.content.Context
import com.example.splinterlandstest.models.CardFoilUrl

sealed class BattlesViewState(open val isRefreshing: Boolean) {
    abstract val onRefresh: (context: Context) -> Unit

    data class Loading(override val onRefresh: (context: Context) -> Unit) : BattlesViewState(true)
    data class Success(
        override val onRefresh: (context: Context) -> Unit,
        override val isRefreshing: Boolean,
        val battles: List<BattleViewState>,
        val playerName: String,
        val playerRating: String,
        val focusChests: Int,
        val focusChestUrl: String,
        val focusEndTimestamp: Long,
        val seasonChests: Int,
        val seasonChestUrl: String,
        val seasonEndTimestamp: Long
    ) :
        BattlesViewState(isRefreshing)

    data class Error(override val onRefresh: (context: Context) -> Unit) : BattlesViewState(false)
}

data class BattleViewState(
    val id: String,
    val mana: String,
    val rulesetUrls: List<String>,
    val time: String,
    val matchType: String,
    val player1Name: String,
    val player1Rating: String,
    val player1CardUrls: List<CardFoilUrl>,
    val player2Name: String,
    val player2Rating: String,
    val player2CardUrls: List<CardFoilUrl>,
    val isWin: Boolean
)