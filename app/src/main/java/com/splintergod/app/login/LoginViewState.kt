package com.splintergod.app.login

sealed class LoginViewState {

    data class CouldNotFindPlayerError(
        val player: String,
        val onAddPlayer: (player: String) -> Unit,
        val onClickBack: () -> Unit
    ) : LoginViewState()

    data class Loading() : LoginViewState()

    data class Success(
        val players: List<LoginViewModel.PlayerRowInfo>,
        val onDeletePlayer: (player: String) -> Unit,
        val onAddPlayer: (player: String) -> Unit
    ) : LoginViewState()

    data class Error(val message: String? = null) : LoginViewState()
}