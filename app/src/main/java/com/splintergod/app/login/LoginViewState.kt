package com.splintergod.app.login

sealed class LoginViewState {
    abstract val onRefresh: () -> Unit

    data class CouldNotFindPlayerError(
        override val onRefresh: () -> Unit,
        val player: String,
        val onAddPlayer: (player: String) -> Unit,
        val onClickBack: () -> Unit
    ) : LoginViewState()

    data class Loading(override val onRefresh: () -> Unit) : LoginViewState()

    data class Success(
        override val onRefresh: () -> Unit,
        val players: List<LoginViewModel.PlayerRowInfo>,
        val onDeletePlayer: (player: String) -> Unit,
        val onAddPlayer: (player: String) -> Unit
    ) : LoginViewState()
}