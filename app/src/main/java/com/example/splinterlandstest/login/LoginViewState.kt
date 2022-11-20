package com.example.splinterlandstest.login

import android.content.Context

sealed class LoginViewState {
    abstract val onRefresh: (context: Context) -> Unit

    data class CouldNotFindPlayerError(
        override val onRefresh: (context: Context) -> Unit,
        val player: String,
        val onAddPlayer: (player: String) -> Unit,
        val onClickBack: () -> Unit
    ) : LoginViewState()

    data class Loading(override val onRefresh: (context: Context) -> Unit) : LoginViewState()
    data class Success(
        override val onRefresh: (context: Context) -> Unit,
        val players: List<LoginViewModel.PlayerRowInfo>,
        val isRefreshing: Boolean,
        val onDeletePlayer: (player: String) -> Unit,
        val onAddPlayer: (player: String) -> Unit
    ) : LoginViewState()
}