package com.example.splinterlandstest.balances

import android.content.Context
import com.example.splinterlandstest.models.BalancesResponse

sealed class BalancesViewState {
    abstract val onRefresh: (context: Context) -> Unit

    data class Loading(override val onRefresh: (context: Context) -> Unit) : BalancesViewState()
    data class Success(override val onRefresh: (context: Context) -> Unit, val balances: List<BalancesResponse>) : BalancesViewState()
    data class Error(override val onRefresh: (context: Context) -> Unit) : BalancesViewState()
}