package com.splintergod.app.collection

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

sealed class CollectionViewState(open val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : CollectionViewState(true)
    data class Success(
        override val onRefresh: () -> Unit,
        val cards: List<CardViewState>,
        val filterRarityStates: List<FilterState.Rarity>,
        val onClickRarity: (String) -> Unit,
        val filterEditionStates: List<FilterState.Edition>,
        val onClickEdition: (String) -> Unit,
        val filterElementStates: List<FilterState.Basic>,
        val onClickElement: (String) -> Unit,
        val filterFoilStates: List<FilterState.Basic>,
        val onClickFoil: (String) -> Unit,
        val filterRoleStates: List<FilterState.Basic>,
        val onClickRole: (String) -> Unit,
        val sortingElementStates: List<SortingState>,
        val selectedSorting: SortingState?,
        val onClickSorting: (CollectionViewModel.Sorting) -> Unit
    ) : CollectionViewState(false)

    data class Error(override val onRefresh: () -> Unit) : CollectionViewState(false)
}

sealed class FilterState(open val id: String, open var selected: Boolean = false) {
    data class Basic(override var id: String, @DrawableRes val imageRes: Int) : FilterState(id)
    data class Rarity(val rarity: Int, val color: Color) : FilterState(rarity.toString())
    data class Edition(val edition: Int, @DrawableRes val imageRes: Int) : FilterState(edition.toString())
}

data class CardViewState(
    val imageUrl: String,
    @DrawableRes val placeHolderRes: Int,
    val quantity: Int,
    val cardId: String,
    val level: Int,
    val edition: Int)

data class SortingState(
    val id: CollectionViewModel.Sorting,
    var name: String = "",
    var selected: Boolean = false) {
    init {
        name = id.name.lowercase().replaceFirstChar { it.uppercase() }
    }
}