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
    private var filterFoilState = listOf(
        FilterFoilState("Regular", imageRes = R.drawable.foil_standard),
        FilterFoilState("Gold", imageRes = R.drawable.foil_gold)
    )
    private var filterRoleState = listOf(
        FilterRoleState("Monster", imageRes = R.drawable.role_monster),
        FilterRoleState("Summoner", imageRes = R.drawable.role_summoner)
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

    private fun onClickFoil(foil: String) {
        filterFoilState.firstOrNull { it.id == foil }?.let {
            it.selected = it.selected.not()
        }

        updateState()
    }

    private fun onClickRole(foil: String) {
        filterRoleState.firstOrNull { it.id == foil }?.let {
            it.selected = it.selected.not()
        }

        updateState()
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
        val foils = filterFoilState.filter { it.selected }.map { it.id }
        val roles = filterRoleState.filter { it.selected }.map { it.id }

        val cards = unfilteredCollection.mapNotNull { card ->
            val cardDetail = cardDetails.firstOrNull { it.id == card.cardDetailId }
            if (cardDetail != null &&
                (rarities.isEmpty() || rarities.contains(cardDetail.rarity)) &&
                (editions.isEmpty() || editions.contains(card.edition)) &&
                (elements.isEmpty() || elements.contains(cardDetail.color)) &&
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
                quantity = 1
            )
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
            },
            filterFoilStates = filterFoilState,
            onClickFoil = {
                onClickFoil(it)
            },
            filterRoleStates = filterRoleState,
            onClickRole = {
                onClickRole(it)
            },
            sortingElementStates = sortingStates,
            selectedSorting = sortingStates.firstOrNull { it.id == currentSorting },
            onClickSorting = {
                onClickSorting(it)
            }
        )
    }
}