package com.splintergod.app.collection

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.R
import com.splintergod.app.Requests
import com.splintergod.app.Session
import com.splintergod.app.models.Card
import com.splintergod.app.models.CardDetail
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<CollectionViewState>(CollectionViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private var unfilteredCollection = listOf<Card>()
    private var cardDetails = listOf<CardDetail>()

    private var filterRaritiesStates = listOf(
        FilterState.Rarity(1, color = Color(0XFFbfd1da)),
        FilterState.Rarity(2, color = Color(0XFF7ac2ff)),
        FilterState.Rarity(3, color = Color(0XFFca6eeb)),
        FilterState.Rarity(4, color = Color(0XFFf3c059))
    )
    private var filterEditionState = listOf(
        FilterState.Edition(0, imageRes = R.drawable.ic_icon_edition_alpha),
        FilterState.Edition(1, imageRes = R.drawable.ic_icon_edition_beta),
        FilterState.Edition(2, imageRes = R.drawable.ic_icon_edition_promo),
        FilterState.Edition(3, imageRes = R.drawable.ic_icon_edition_reward),
        FilterState.Edition(4, imageRes = R.drawable.ic_icon_edition_untamed),
        FilterState.Edition(5, imageRes = R.drawable.ic_icon_edition_dice),
        FilterState.Edition(6, imageRes = R.drawable.ic_icon_edition_gladius),
        FilterState.Edition(7, imageRes = R.drawable.ic_icon_edition_chaos),
        FilterState.Edition(8, imageRes = R.drawable.ic_icon_edition_rift)
    )
    private var filterElementState = listOf(
        FilterState.Basic("Red", imageRes = R.drawable.element_fire),
        FilterState.Basic("Blue", imageRes = R.drawable.element_water),
        FilterState.Basic("Green", imageRes = R.drawable.element_earth),
        FilterState.Basic("White", imageRes = R.drawable.element_life),
        FilterState.Basic("Black", imageRes = R.drawable.element_death),
        FilterState.Basic("Gold", imageRes = R.drawable.element_dragon),
        FilterState.Basic("Gray", imageRes = R.drawable.element_neutral)
    )
    private var filterFoilState = listOf(
        FilterState.Basic("Regular", imageRes = R.drawable.foil_standard),
        FilterState.Basic("Gold", imageRes = R.drawable.foil_gold)
    )
    private var filterRoleState = listOf(
        FilterState.Basic("Monster", imageRes = R.drawable.role_monster),
        FilterState.Basic("Summoner", imageRes = R.drawable.role_summoner)
    )

    enum class Sorting {
        ID,
        NAME,
        MANA,
        SPEED,
        HEALTH,
        LEVEL,
        MAGIC,
        RANGE,
        MELEE
    }

    private var sortingStates = listOf(
        SortingState(Sorting.ID, selected = true),
        // SortingState(Sorting.NAME),
        SortingState(Sorting.MANA),
        SortingState(Sorting.SPEED),
        SortingState(Sorting.HEALTH),
        SortingState(Sorting.LEVEL),
        SortingState(Sorting.MAGIC),
        SortingState(Sorting.RANGE),
        SortingState(Sorting.MELEE)
    )

    private var currentSorting: Sorting = Sorting.ID

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        onRefresh(false)
    }

    fun onRefresh(forceRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = CollectionViewState.Loading { onRefresh() }

            unfilteredCollection = cache.getCollection(session.player)
            if (unfilteredCollection.isEmpty() || forceRefresh) {
                unfilteredCollection = requests.getCollection(session.player)
            }

            cardDetails = cache.getCardDetails()
            if (cardDetails.isEmpty() || forceRefresh) {
                cardDetails = requests.getCardDetails()
            }
            updateState()
        }
    }

    private fun onClickSorting(sorting: Sorting) {
        currentSorting = sorting
        sortingStates.forEach {
            it.selected = it.id == currentSorting
        }

        updateState()
    }

    private fun onClickFilter(filterStates: List<FilterState>, id: String) {
        filterStates.firstOrNull { it.id == id }?.let {
            it.selected = !it.selected
        }

        updateState()
    }

    private fun updateState() {
        val rarities = filterRaritiesStates.filter { it.selected }.map { it.rarity }
        val editions = filterEditionState.filter { it.selected }.map { it.edition }
        val elements = filterElementState.filter { it.selected }.map { it.id }
        val foils = filterFoilState.filter { it.selected }.map { it.id }
        val roles = filterRoleState.filter { it.selected }.map { it.id }

        val cards = unfilteredCollection.mapNotNull { card ->
            val cardDetail = cardDetails.firstOrNull { it.id == card.cardDetailId }
            if (cardDetail != null &&
                (rarities.isEmpty() || rarities.contains(cardDetail.rarity)) &&
                (editions.isEmpty() || editions.contains(card.edition)) &&
                (elements.isEmpty() || elements.contains(cardDetail.color) || elements.contains(cardDetail.secondary_color)) &&
                (roles.isEmpty() || roles.contains(cardDetail.type)) &&
                (foils.isEmpty() || foils.contains(card.getFoilId()))
            ) {
                card.setStats(cardDetail)
                card
            } else {
                null
            }
        }.sortedByDescending { card ->
            when (currentSorting) {
                Sorting.MANA -> card.mana
                Sorting.SPEED -> card.speed
                Sorting.HEALTH -> card.health
                Sorting.LEVEL -> card.level
                Sorting.MAGIC -> card.magic
                Sorting.RANGE -> card.range
                Sorting.MELEE -> card.melee
                Sorting.NAME -> 0
                Sorting.ID -> card.cardDetailId.toIntOrNull()
            }
        }.map { card ->
            CardViewState(
                imageUrl = card.imageUrl,
                placeHolderRes = card.getPlaceholderDrawable(),
                quantity = card.regularLevels.size + card.goldLevels.size,
                cardId = card.cardDetailId,
                level = card.level,
                edition = card.edition
            )
        }

        _state.value = CollectionViewState.Success(
            onRefresh = { onRefresh() },
            cards = cards,
            filterRarityStates = filterRaritiesStates,
            onClickRarity = {
                onClickFilter(filterRaritiesStates, it)
            },
            filterEditionStates = filterEditionState,
            onClickEdition = {
                onClickFilter(filterEditionState, it)
            },
            filterElementStates = filterElementState,
            onClickElement = {
                onClickFilter(filterElementState, it)
            },
            filterFoilStates = filterFoilState,
            onClickFoil = {
                onClickFilter(filterFoilState, it)
            },
            filterRoleStates = filterRoleState,
            onClickRole = {
                onClickFilter(filterRoleState, it)
            },
            sortingElementStates = sortingStates,
            selectedSorting = sortingStates.firstOrNull { it.id == currentSorting },
            onClickSorting = {
                onClickSorting(it)
            }
        )
    }
}