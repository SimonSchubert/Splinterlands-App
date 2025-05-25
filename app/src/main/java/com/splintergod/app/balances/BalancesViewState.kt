package com.splintergod.app.balances

import com.splintergod.app.models.Balances

sealed class BalancesViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : BalancesViewState(true)
    data class Success(override val onRefresh: () -> Unit, val balances: List<Balances>) :
        BalancesViewState(false)

    data class Error(override val onRefresh: () -> Unit) : BalancesViewState(false)
}