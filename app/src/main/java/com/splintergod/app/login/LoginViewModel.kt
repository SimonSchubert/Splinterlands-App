package com.splintergod.app.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.splintergod.app.Session

class LoginViewModel(val cache: Cache, val requests: Requests, val session: Session) : ViewModel() {

    private val _state = MutableStateFlow<LoginViewState>(LoginViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val players = mutableListOf<PlayerRowInfo>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    data class PlayerRowInfo(
        val name: String,
        var chests: Int = 2,
        var timeLeft: String = "",
        var chestUrl: String = ""
    )

    fun loadPlayerData() {
        players.clear()
        players.addAll(cache.getPlayerList().map { PlayerRowInfo(it) })

        updateSuccessState(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            players.forEach { player ->
                val questInfo = requests.getRewardsInfo(player.name)
                player.chests = questInfo.questRewardInfo.chestEarned
                player.chestUrl = questInfo.questRewardInfo.getChestUrl()
                player.timeLeft = questInfo.questRewardInfo.getFormattedEndDateShort()
                updateSuccessState(true)
            }
            updateSuccessState(false)
        }
    }

    private fun updateSuccessState(isRefreshing: Boolean) {
        _state.value = LoginViewState.Success(players = players,
            isRefreshing = isRefreshing,
            onDeletePlayer = { onDelete(it) },
            onAddPlayer = { onAddPlayer(it) },
            onRefresh = { onRefresh() }
        )
    }

    private fun onRefresh() {
        loadPlayerData()
    }

    private fun onDelete(player: String) {
        cache.deletePlayerFromList(player)
        loadPlayerData()
    }

    private fun onAddPlayer(player: String) {
        _state.value = LoginViewState.Loading { onRefresh() }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            if (requests.getPlayerDetails(player).name == null) { // gson serializes missing as null
                _state.value = LoginViewState.CouldNotFindPlayerError(
                    player = player,
                    onAddPlayer = { onAddPlayer(it) },
                    onRefresh = { onRefresh() },
                    onClickBack = { loadPlayerData() })
            } else {
                cache.writePlayerToList(player)
                session.setCurrentPlayer(player) // Set current player in session
                loadPlayerData() // This will refresh the list and UI
            }
        }
    }
}