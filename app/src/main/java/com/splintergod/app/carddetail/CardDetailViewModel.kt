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

class CardDetailViewModel(val session: Session, val cache: Cache, val requests: Requests) :
    ViewModel() {

    private val _state =
        MutableStateFlow<CardDetailViewState>(CardDetailViewState.Loading()) // Default onRefresh can call loadCardFromSession or a specific load
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _cardNameStateFlow = MutableStateFlow("")
    val cardNameStateFlow = _cardNameStateFlow.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = CardDetailViewState.Error(throwable.message ?: "Failed to load card details.")
        _isRefreshing.value = false // Ensure refreshing is stopped on error
    }

    fun loadCard(cardId: String, level: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true
            _state.value = CardDetailViewState.Loading()
            try {
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

                    val card = Card(
                        cardDetail.id,
                        cardDetail.editions.split(",").first().toInt(),
                        false,
                        level
                    )
                    card.setStats(cardDetail)

                    val cards = cache.getCollection(session.player)
                    cards.firstOrNull { it.cardDetailId == cardDetail.id }?.let {
                        card.goldLevels = it.goldLevels
                        card.regularLevels = it.regularLevels
                    }

                    _cardNameStateFlow.value = card.name

                    _state.value = CardDetailViewState.Success(
                        colorIcon = colorIcon,
                        card = card,
                        cardDetail = cardDetail,
                        abilities = cache.getAbilities()
                    )
                } else {
                _state.value = CardDetailViewState.Error("Card detail not found.")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Renamed the original loadCard to loadCardFromSession for clarity if needed by onRefresh in Content
    fun loadCardFromSession() {
        if (session.currentCardDetailId.isNotEmpty()) {
            loadCard(session.currentCardDetailId, session.currentCardDetailLevel)
        } else {
            // Handle case where session might not have card details (e.g. direct link or error)
            _state.value = CardDetailViewState.Error("No card selected in session.")
        }
    }
}