package com.splintergod.app.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import com.splintergod.app.Session
import com.splintergod.app.models.Card
import com.splintergod.app.models.CardReward
import com.splintergod.app.models.RewardGroup
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsViewModel(val session: Session, val cache: Cache, val requests: Requests) :
    ViewModel() {

    private val _state =
        MutableStateFlow<RewardsViewState>(RewardsViewState.Loading())
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = RewardsViewState.Error(throwable.message ?: "Failed to load rewards.")
        _isRefreshing.value = false // Ensure refreshing is stopped on error
    }

    init {
        onRefresh()
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true
            _state.value = RewardsViewState.Loading()
            try {
                var cardDetails = cache.getCardDetails()
                if (cardDetails.isEmpty()) {
                cardDetails = requests.getCardDetails()
            }

            val players = if (session.player.isNotEmpty()) {
                listOf(session.player)
            } else {
                cache.getPlayerList()
            }

            val rewardGroups = mutableListOf<RewardGroup>()

            players.forEach { player ->
                val rewardGroup = requests.getRecentRewards(player)
                if (rewardGroup != null) {

                    rewardGroup.rewards.forEach {
                        if (it is CardReward) {
                            val card = Card(it.cardId.toString(), it.edition, it.isGold, 1)
                            val cardDetail = cardDetails.firstOrNull { it.id == card.cardDetailId }
                            if (cardDetail != null) {
                                card.setStats(cardDetail)
                                it.url = card.imageUrl
                                it.name = cardDetail.name
                            }
                        }
                    }
                    rewardGroup.player = player
                    rewardGroups.add(rewardGroup)
                    rewardGroups.sortBy { it.getSecondsAgo() }

                    _state.value = RewardsViewState.Success(
                        rewardsGroups = rewardGroups.toList()
                    )
                }
            }

                // Consolidate state updates to after the loop and data processing.
                if (rewardGroups.isNotEmpty()) {
                    _state.value = RewardsViewState.Success(
                        rewardsGroups = rewardGroups.toList().sortedBy { it.getSecondsAgo() }
                    )
                } else {
                    // If players list was empty, or no rewards found for any player
                    _state.value = RewardsViewState.Error("No rewards found.") // Or Success with empty list, depending on desired UX
                }
            } catch (e: Exception) {
                // This will be caught by coroutineExceptionHandler if it's a coroutine exception
                // or if any other exception occurs in the try block.
                _state.value = RewardsViewState.Error(e.message ?: "Failed to process rewards.")
                // Log e
                // No need to re-throw if CEH handles _isRefreshing.value = false
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}