package com.splintergod.app.focuses

import com.splintergod.app.models.Focus

sealed class FocusesViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : FocusesViewState(true)
    data class Success(override val onRefresh: () -> Unit, val focuses: List<Focus>) : FocusesViewState(false)
    data class Error(override val onRefresh: () -> Unit) : FocusesViewState(false)
}