package com.example.splinterlandstest.rewards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsFragmentViewModel(context: Context, player: String) : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    private var _state = MutableStateFlow<List<Requests.Reward>>(emptyList())
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            var cardDetails = cache.getCardDetails(context)
            if (cardDetails.isEmpty()) {
                cardDetails = requests.getCardDetails(context)
            }

            val rewards = requests.getRecentRewards(player)
            rewards.forEach {
                if (it is Requests.CardReward) {
                    val card = Requests.Card(it.cardId.toString(), 3, it.isGold)
                    val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }
                    if (cardDetail != null) {
                        it.url = card.getPath(cardDetail)
                        it.name = cardDetail.name
                    }
                }
            }

            _state.value = rewards
        }
    }
}

class RewardModelModelFactory(val context: Context, val player: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RewardsFragmentViewModel(context, player) as T
    }
}