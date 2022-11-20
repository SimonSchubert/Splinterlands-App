package com.example.splinterlandstest.battles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.Battle
import com.example.splinterlandstest.models.CardDetail
import com.example.splinterlandstest.models.GameSettings
import com.example.splinterlandstest.models.PlayerDetails
import com.example.splinterlandstest.models.RewardsInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class BattlesViewModel(val player: String, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<BattlesViewState>(BattlesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    fun loadBattles(player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            updateReadyState(
                isRefreshing = true,
                rewardsInfo = cache.getRewardsInfo(player),
                playerDetails = cache.getPlayerDetails(player),
                gameSettings = cache.getSettings(),
                battles = cache.getBattleHistory(player),
                cardDetails = cache.getCardDetails()
            )

            updateReadyState(
                isRefreshing = false,
                rewardsInfo = requests.getRewardsInfo(player),
                playerDetails = requests.getPlayerDetails(player),
                gameSettings = requests.getSettings(),
                battles = requests.getBattleHistory(player),
                cardDetails = requests.getCardDetails()
            )
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = BattlesViewState.Loading { onRefresh() }

            updateReadyState(
                isRefreshing = false,
                rewardsInfo = requests.getRewardsInfo(player),
                playerDetails = requests.getPlayerDetails(player),
                gameSettings = requests.getSettings(),
                battles = requests.getBattleHistory(player),
                cardDetails = requests.getCardDetails()
            )
        }
    }

    private fun updateReadyState(
        isRefreshing: Boolean,
        rewardsInfo: RewardsInfo?,
        playerDetails: PlayerDetails?,
        gameSettings: GameSettings?,
        battles: List<Battle>,
        cardDetails: List<CardDetail>
    ) {
        if (rewardsInfo != null && playerDetails != null && gameSettings != null) {

            val battleViewStates = battles.map {
                BattleViewState(
                    id = it.battleQueueId1,
                    mana = it.manaCap.toString(),
                    rulesetUrls = it.getRulesetImageUrls(),
                    time = it.getTimeAgo(),
                    matchType = it.getType(),
                    player1Name = player.uppercase(),
                    player1Rating = it.getOwnRating(player),
                    player1CardUrls = it.getOwnDetail(player)?.getCardUrls(cardDetails) ?: emptyList(),
                    player2Name = it.getOpponent(player),
                    player2Rating = it.getOpponentRating(player),
                    player2CardUrls = it.getOpponentDetail(player)?.getCardUrls(cardDetails) ?: emptyList(),
                    isWin = it.isWin(player)
                )
            }

            _state.value = BattlesViewState.Success(
                onRefresh = { onRefresh() },
                isRefreshing = isRefreshing,
                battles = battleViewStates,
                playerName = playerDetails.name.uppercase(),
                playerRating = "W: ${numberFormat.format(playerDetails.rating)}, M: ${numberFormat.format(playerDetails.modernRating)}",
                focusChests = rewardsInfo.questRewardInfo.chestEarned,
                focusChestUrl = rewardsInfo.questRewardInfo.getChestUrl(),
                focusEndTimestamp = rewardsInfo.questRewardInfo.getEndTimestamp(),
                seasonChests = rewardsInfo.seasonRewardInfo.chestEarned,
                seasonChestUrl = rewardsInfo.seasonRewardInfo.getChestUrl(),
                seasonEndTimestamp = gameSettings.season.getEndTimestamp()
            )
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}

class BattlesViewModelFactory(val player: String, val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BattlesViewModel(player, cache, requests) as T
    }
}