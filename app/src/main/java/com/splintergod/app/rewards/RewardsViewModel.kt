package com.splintergod.app.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import com.splintergod.app.models.Card
import com.splintergod.app.models.CardReward
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsViewModel(val player: String, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<RewardsViewState>(RewardsViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = RewardsViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        onRefresh()
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = RewardsViewState.Loading { onRefresh() }

            var cardDetails = cache.getCardDetails()
            if (cardDetails.isEmpty()) {
                cardDetails = requests.getCardDetails()
            }

            val rewards = requests.getRecentRewards(player)
            rewards.forEach {
                if (it is CardReward) {
                    val card = Card(it.cardId.toString(), 3, it.isGold, 1)
                    val cardDetail = cardDetails.firstOrNull { it.id == card.cardDetailId }
                    if (cardDetail != null) {
                        card.setStats(cardDetail)
                        it.url = card.imageUrl
                        it.name = cardDetail.name
                    }
                }
            }
            if (rewards.isEmpty()) {
                _state.value = RewardsViewState.Error { onRefresh() }
            } else {
                _state.value = RewardsViewState.Success(
                    onRefresh = { onRefresh() },
                    rewards = rewards
                )
            }
        }
    }
}

class RewardsViewModelFactory(val player: String, val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RewardsViewModel(player, cache, requests) as T
    }
}