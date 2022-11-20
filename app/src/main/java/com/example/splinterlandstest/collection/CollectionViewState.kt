package com.example.splinterlandstest.collection

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

sealed class CollectionViewState(open val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : CollectionViewState(true)
    data class Success(
        override val onRefresh: () -> Unit,
        val cards: List<CardViewState>,
        val filterRarityStates: List<FilterRarityState>,
        val onClickRarity: (Int) -> Unit,
        val filterEditionStates: List<FilterEditionState>,
        val onClickEdition: (Int) -> Unit,
        val filterElementStates: List<FilterElementState>,
        val onClickElement: (String) -> Unit,
        val sortingElementStates: List<SortingState>,
        val selectedSorting: SortingState?,
        val onClickSorting: (CollectionViewModel.Sorting) -> Unit
    ) : CollectionViewState(false)

    data class Error(override val onRefresh: () -> Unit) : CollectionViewState(false)
}

data class CardViewState(val imageUrl: String, @DrawableRes val placeHolderRes: Int, val quantity: Int)
data class FilterRarityState(val id: Int, var selected: Boolean = false, val color: Color)
data class FilterEditionState(val id: Int, var selected: Boolean = false, @DrawableRes val imageRes: Int)
data class FilterElementState(val id: String, var selected: Boolean = false, @DrawableRes val imageRes: Int)
data class SortingState(val id: CollectionViewModel.Sorting, var name: String = "", var selected: Boolean = false) {
    init {
        name = id.name.lowercase().replaceFirstChar { it.uppercase() }
    }
}