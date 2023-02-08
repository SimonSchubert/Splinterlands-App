package com.splintergod.app.carddetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.R
import com.splintergod.app.Requests
import com.splintergod.app.Session
import com.splintergod.app.models.Card
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardDetailViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<CardDetailViewState>(CardDetailViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    var cardName = mutableStateOf("")

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = CardDetailViewState.Error { onRefresh() }
    }

    fun loadCard() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = CardDetailViewState.Loading { onRefresh() }

            val cardDetails = cache.getCardDetails()
            val cardDetail = cardDetails.firstOrNull { it.id == session.currentCardDetailId }


            if (cardDetail != null) {
                val colorIcon = when (cardDetail.color) {
                    "Red" -> R.drawable.element_fire
                    "Blue" -> R.drawable.element_water
                    "Green" -> R.drawable.element_earth
                    "White" -> R.drawable.element_life
                    "Black" -> R.drawable.element_death
                    "Gold" -> R.drawable.element_dragon
                    "Gray" -> R.drawable.element_neutral
                    else -> R.drawable.asset_dec
                }

                val card = Card(cardDetail.id, cardDetail.editions.split(",").first().toInt(), false, 1)
                card.setStats(cardDetail)

                _state.value = CardDetailViewState.Success(
                    onRefresh = { onRefresh() },
                    colorIcon = colorIcon,
                    card = card,
                    cardDetail = cardDetail
                )
            } else {
                _state.value = CardDetailViewState.Error(
                    onRefresh = { onRefresh() }
                )
            }
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = CardDetailViewState.Loading { onRefresh() }

        }
    }
}