package com.example.splinterlandstest.focuses

import com.example.splinterlandstest.models.Focus

sealed class FocusesViewState {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : FocusesViewState()
    data class Success(override val onRefresh: () -> Unit, val focuses: List<Focus>) : FocusesViewState()
    data class Error(override val onRefresh: () -> Unit) : FocusesViewState()
}