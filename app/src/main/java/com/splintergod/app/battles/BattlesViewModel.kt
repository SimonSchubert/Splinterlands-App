package com.splintergod.app.battles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import com.splintergod.app.Session
import com.splintergod.app.models.Battle
import com.splintergod.app.models.CardDetail
import com.splintergod.app.models.GameSettings
import com.splintergod.app.models.PlayerDetails
import com.splintergod.app.models.RewardsInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class BattlesViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<BattlesViewState>(BattlesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    private var rewardsInfo: RewardsInfo? = null
    private var playerDetails: PlayerDetails? = null
    private var gameSettings: GameSettings? = null
    private var battles: List<Battle> = emptyList()
    private var cardDetails: List<CardDetail> = emptyList()

    fun loadBattles() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            rewardsInfo = cache.getRewardsInfo(session.player)
            playerDetails = cache.getPlayerDetails(session.player)
            gameSettings = cache.getSettings()
            battles = cache.getBattleHistory(session.player)
            cardDetails = cache.getCardDetails()

            updateReadyState(
                isRefreshing = true
            )

            battles = requests.getBattleHistory(session.player)

            updateReadyState(
                isRefreshing = true
            )

            rewardsInfo = requests.getRewardsInfo(session.player)

            updateReadyState(
                isRefreshing = true
            )

            playerDetails = requests.getPlayerDetails(session.player)

            updateReadyState(
                isRefreshing = true
            )

            gameSettings = requests.getSettings()
            cardDetails = requests.getCardDetails()

            updateReadyState(
                isRefreshing = false
            )
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = BattlesViewState.Loading { onRefresh() }

            rewardsInfo = requests.getRewardsInfo(session.player)
            playerDetails = requests.getPlayerDetails(session.player)
            gameSettings = requests.getSettings()
            battles = requests.getBattleHistory(session.player)
            cardDetails = requests.getCardDetails()

            updateReadyState(
                isRefreshing = false,
            )
        }
    }

    private fun updateReadyState(
        isRefreshing: Boolean
    ) {
        val currentRewardsInfo = rewardsInfo
        val currentPlayerDetails = playerDetails
        val currentGameSettings = gameSettings
        if (currentRewardsInfo != null && currentPlayerDetails != null && currentGameSettings != null) {

            val battleViewStates = battles.map {
                BattleViewState(
                    id = it.battleQueueId1,
                    mana = it.manaCap.toString(),
                    rulesetUrls = it.getRulesetImageUrls(),
                    time = it.getTimeAgo(),
                    matchType = it.getType(),
                    player1Name = session.player.uppercase(),
                    player1Rating = it.getOwnRating(session.player),
                    player1CardUrls = it.getOwnDetail(session.player)?.getCardUrls(cardDetails) ?: emptyList(),
                    player2Name = it.getOpponent(session.player),
                    player2Rating = it.getOpponentRating(session.player),
                    player2CardUrls = it.getOpponentDetail(session.player)?.getCardUrls(cardDetails) ?: emptyList(),
                    isWin = it.isWin(session.player)
                )
            }

            _state.value = BattlesViewState.Success(
                onRefresh = { onRefresh() },
                isRefreshing = isRefreshing,
                battles = battleViewStates,
                playerName = currentPlayerDetails.name.uppercase(),
                playerRating = "W: ${numberFormat.format(currentPlayerDetails.rating)}, M: ${numberFormat.format(currentPlayerDetails.modernRating)}",
                focusChests = currentRewardsInfo.questRewardInfo.chestEarned,
                focusChestUrl = currentRewardsInfo.questRewardInfo.getChestUrl(),
                focusEndTimestamp = currentRewardsInfo.questRewardInfo.getEndTimestamp(),
                seasonChests = currentRewardsInfo.seasonRewardInfo.chestEarned,
                seasonChestUrl = currentRewardsInfo.seasonRewardInfo.getChestUrl(),
                seasonEndTimestamp = currentGameSettings.season.getEndTimestamp()
            )
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = BattlesViewState.Error { onRefresh() }
    }
}