package com.example.splinterlandstest.focuses

import com.example.splinterlandstest.models.Focus

sealed class FocusesViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : FocusesViewState(true)
    data class Success(override val onRefresh: () -> Unit, val focuses: List<Focus>) : FocusesViewState(false)
    data class Error(override val onRefresh: () -> Unit) : FocusesViewState(false)
}