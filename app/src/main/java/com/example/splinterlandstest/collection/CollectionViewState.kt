package com.example.splinterlandstest.collection

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

sealed class CollectionViewState {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : CollectionViewState()
    data class Success(
        override val onRefresh: () -> Unit,
        val cards: List<CardViewState>,
        val filterRarityStates: List<FilterRarityState>,
        val onClickRarity: (Int) -> Unit,
        val filterEditionStates: List<FilterEditionState>,
        val onClickEdition: (Int) -> Unit,
        val filterElementStates: List<FilterElementState>,
        val onClickElement: (String) -> Unit
    ) : CollectionViewState()

    data class Error(override val onRefresh: () -> Unit) : CollectionViewState()
}

data class CardViewState(val imageUrl: String, @DrawableRes val placeHolderRes: Int, val quantity: Int)
data class FilterRarityState(val id: Int, var selected: Boolean = false, val color: Color)
data class FilterEditionState(val id: Int, var selected: Boolean = false, @DrawableRes val imageRes: Int)
data class FilterElementState(val id: String, var selected: Boolean = false, @DrawableRes val imageRes: Int)