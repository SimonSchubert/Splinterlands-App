package com.splintergod.app.carddetail

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

    private val _state = MutableStateFlow<CardDetailViewState>(CardDetailViewState.Loading { loadCardFromSession() }) // Default onRefresh can call loadCardFromSession or a specific load
    val state = _state.asStateFlow()

    private val _cardNameStateFlow = MutableStateFlow("")
    val cardNameStateFlow = _cardNameStateFlow.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = CardDetailViewState.Error { loadCardFromSession() } // Default onRefresh
    }

    fun loadCard(cardId: String, level: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = CardDetailViewState.Loading { loadCard(cardId, level) } // Specific onRefresh

            // Set session values if needed by other parts of the app, though CardDetailScreen uses arguments
            session.currentCardDetailId = cardId
            session.currentCardDetailLevel = level

            val cardDetails = cache.getCardDetails()
            val cardDetail = cardDetails.firstOrNull { it.id == cardId }


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

                val card = Card(cardDetail.id, cardDetail.editions.split(",").first().toInt(), false, level)
                card.setStats(cardDetail)

                val cards = cache.getCollection(session.player)
                cards.firstOrNull { it.cardDetailId == cardDetail.id }?.let {
                    card.goldLevels = it.goldLevels
                    card.regularLevels = it.regularLevels
                }

                _cardNameStateFlow.value = card.name

                _state.value = CardDetailViewState.Success(
                    onRefresh = { loadCard(cardId, level) }, // Specific onRefresh
                    colorIcon = colorIcon,
                    card = card,
                    cardDetail = cardDetail,
                    abilities = cache.getAbilities()
                )
            } else {
                _state.value = CardDetailViewState.Error(
                    onRefresh = { loadCard(cardId, level) } // Specific onRefresh
                )
            }
        }
    }

    // Renamed the original loadCard to loadCardFromSession for clarity if needed by onRefresh in Content
    fun loadCardFromSession() {
        if (session.currentCardDetailId.isNotEmpty()) {
            loadCard(session.currentCardDetailId, session.currentCardDetailLevel)
        } else {
            // Handle case where session might not have card details (e.g. direct link or error)
            _state.value = CardDetailViewState.Error { /* Decide on a refresh strategy */ }
        }
    }
}