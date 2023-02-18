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

class RewardsViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<RewardsViewState>(RewardsViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = RewardsViewState.Error { onRefresh() }
    }

    init {
        onRefresh()
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = RewardsViewState.Loading { onRefresh() }

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
                        onRefresh = { onRefresh() },
                        isRefreshing = true,
                        rewardsGroups = rewardGroups.toList()
                    )
                }
            }

            _state.value = RewardsViewState.Success(
                onRefresh = { onRefresh() },
                isRefreshing = false,
                rewardsGroups = rewardGroups.toList()
            )

            if (rewardGroups.isEmpty()) {
                _state.value = RewardsViewState.Error { onRefresh() }
            }

        }
    }
}