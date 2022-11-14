package com.example.splinterlandstest.collection

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(val player: String, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<CollectionViewState>(CollectionViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private var unfilteredCollection = listOf<Card>()
    private var cardDetails = listOf<CardDetail>()

    private var filterRaritiesStates = listOf(
        FilterRarityState(1, color = Color(0XFFbfd1da)),
        FilterRarityState(2, color = Color(0XFF7ac2ff)),
        FilterRarityState(3, color = Color(0XFFca6eeb)),
        FilterRarityState(4, color = Color(0XFFf3c059))
    )
    private var filterEditionState = listOf(
        FilterEditionState(0, imageRes = R.drawable.ic_icon_edition_alpha),
        FilterEditionState(1, imageRes = R.drawable.ic_icon_edition_beta),
        FilterEditionState(2, imageRes = R.drawable.ic_icon_edition_promo),
        FilterEditionState(3, imageRes = R.drawable.ic_icon_edition_reward),
        FilterEditionState(4, imageRes = R.drawable.ic_icon_edition_untamed),
        FilterEditionState(5, imageRes = R.drawable.ic_icon_edition_dice),
        FilterEditionState(6, imageRes = R.drawable.ic_icon_edition_gladius),
        FilterEditionState(7, imageRes = R.drawable.ic_icon_edition_chaos),
        FilterEditionState(8, imageRes = R.drawable.ic_icon_edition_rift)
    )
    private var filterElementState = listOf(
        FilterElementState("Red", imageRes = R.drawable.element_fire),
        FilterElementState("Blue", imageRes = R.drawable.element_water),
        FilterElementState("Green", imageRes = R.drawable.element_earth),
        FilterElementState("White", imageRes = R.drawable.element_life),
        FilterElementState("Black", imageRes = R.drawable.element_death),
        FilterElementState("Gold", imageRes = R.drawable.element_dragon),
        FilterElementState("Gray", imageRes = R.drawable.element_neutral)
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        onRefresh(false)
    }

    fun onRefresh(forceRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = CollectionViewState.Loading { onRefresh() }

            unfilteredCollection = cache.getCollection(player)
            if (unfilteredCollection.isEmpty() || forceRefresh) {
                unfilteredCollection = requests.getCollection(player)
            }
            cardDetails = cache.getCardDetails()
            if (cardDetails.isEmpty() || forceRefresh) {
                cardDetails = requests.getCardDetails()
            }

            updateState()
        }
    }

    private fun onClickRarity(id: Int) {
        filterRaritiesStates.firstOrNull { it.id == id }?.let {
            it.selected = it.selected.not()
        }

        updateState()
    }

    private fun onClickEdition(id: Int) {
        filterEditionState.firstOrNull { it.id == id }?.let {
            it.selected = it.selected.not()
        }

        updateState()
    }

    private fun onClickElement(id: String) {
        filterElementState.firstOrNull { it.id == id }?.let {
            it.selected = it.selected.not()
        }

        updateState()
    }

    private fun updateState() {
        val rarities = filterRaritiesStates.filter { it.selected }.map { it.id }
        val editions = filterEditionState.filter { it.selected }.map { it.id }
        val elements = filterElementState.filter { it.selected }.map { it.id }

        val cards = unfilteredCollection.mapNotNull { card ->
            val cardDetail = cardDetails.firstOrNull { it.id == card.cardDetailId }
            if (cardDetail != null &&
                (rarities.isEmpty() || rarities.contains(cardDetail.rarity)) &&
                (editions.isEmpty() || editions.contains(card.edition)) &&
                (elements.isEmpty() || elements.contains(cardDetail.color))
            ) {
                val imageUrl = card.getImageUrl(cardDetail)
                CardViewState(
                    imageUrl = imageUrl,
                    placeHolderRes = card.getPlaceholderDrawable(),
                    quantity = 1
                )
            } else {
                null
            }
        }

        _state.value = CollectionViewState.Success(
            onRefresh = { onRefresh() },
            cards = cards,
            filterRarityStates = filterRaritiesStates,
            onClickRarity = {
                onClickRarity(it)
            },
            filterEditionStates = filterEditionState,
            onClickEdition = {
                onClickEdition(it)
            },
            filterElementStates = filterElementState,
            onClickElement = {
                onClickElement(it)
            }
        )
    }
}

class CollectionViewModelFactory(val player: String, val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollectionViewModel(player, cache, requests) as T
    }
}