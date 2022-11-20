package com.example.splinterlandstest.balances

import android.content.Context
import com.example.splinterlandstest.models.Balances

sealed class BalancesViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: (context: Context) -> Unit

    data class Loading(override val onRefresh: (context: Context) -> Unit) : BalancesViewState(true)
    data class Success(override val onRefresh: (context: Context) -> Unit, val balances: List<Balances>) : BalancesViewState(false)
    data class Error(override val onRefresh: (context: Context) -> Unit) : BalancesViewState(false)
}