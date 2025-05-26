package com.splintergod.app.balances

import com.splintergod.app.models.Balances

sealed class BalancesViewState {

    data class Loading() : BalancesViewState()
    data class Success(val balances: List<Balances>) :
        BalancesViewState()

    data class Error(val message: String? = null) : BalancesViewState()
}