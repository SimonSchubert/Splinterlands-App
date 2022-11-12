package com.example.splinterlandstest.collection

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
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

class CollectionFragmentViewModel(val player: String, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<CollectionViewState>(CollectionViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    val collection: MutableLiveData<List<Card>> = MutableLiveData()

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
        FilterElementState("Red", imageRes = R.drawable.icon_element_fire_2),
        FilterElementState("Blue", imageRes = R.drawable.icon_element_water_2),
        FilterElementState("Green", imageRes = R.drawable.icon_element_earth_2),
        FilterElementState("White", imageRes = R.drawable.icon_element_life_2),
        FilterElementState("Black", imageRes = R.drawable.icon_element_death_2),
        FilterElementState("Gold", imageRes = R.drawable.icon_element_dragon_2),
        FilterElementState("Gray", imageRes = R.drawable.icon_element_neutral_2)
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = CollectionViewState.Loading { onRefresh() }

            unfilteredCollection = cache.getCollection(player)
            cardDetails = cache.getCardDetails()

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
            val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }
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


//    fun loadCollection(player: String) {
//        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            unfilteredCollection = cache.getCollection(player)
//            updateCollection()
//            unfilteredCollection = requests.getCollection(player)
//            updateCollection()
//            cardDetails.postValue(requests.getCardDetails())
//        }
//    }

//    private fun updateCollection() {
//        var filteredCollection = unfilteredCollection
//        if (unfilteredCollection.isNotEmpty()) {
//            if (filterRarities.isNotEmpty()) {
//                val cardIds =
//                    cardDetails.value?.filter { filterRarities.contains(it.rarity) }?.map { it.id } ?: emptyList()
//                filteredCollection = filteredCollection.filter { cardIds.contains(it.card_detail_id) }
//            }
//            if (filterEditions.isNotEmpty()) {
//                filteredCollection = filteredCollection.filter { filterEditions.contains(it.edition) }
//            }
//        }
//        collection.postValue(filteredCollection)
//    }

//    fun updateFilter(rarities: List<Int>, editions: List<Int>) {
//        filterRarities = rarities
//        filterEditions = editions
//        updateCollection()
//    }

}

class CollectionViewModelFactory(val player: String, val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollectionFragmentViewModel(player, cache, requests) as T
    }
}