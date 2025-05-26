package com.splintergod.app.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import com.splintergod.app.Session
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(val cache: Cache, val requests: Requests, val session: Session) : ViewModel() {

    private val _state =
        MutableStateFlow<LoginViewState>(LoginViewState.Loading())
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val players = mutableListOf<PlayerRowInfo>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = LoginViewState.Error(throwable.message ?: "An unexpected error occurred.")
        _isRefreshing.value = false
    }

    data class PlayerRowInfo(
        val name: String,
        var chests: Int = 2,
        var timeLeft: String = "",
        var chestUrl: String = ""
    )

    // loadPlayerData is complex due to its dual sync/async nature and multiple state updates.
    // Let's make onRefresh the primary entry point for loading.
    // The original loadPlayerData will be refactored into onRefresh.

    // This function is called by onRefresh, onDelete, onAddPlayer
    // It needs to be idempotent and handle its own state correctly.
    fun loadPlayerData() { // Renaming to onRefresh to make it the primary public refresh action
        onRefresh()
    }

    fun onRefresh() { // This will be the main refresh logic
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true
            _state.value = LoginViewState.Loading()
            try {
                players.clear()
                players.addAll(cache.getPlayerList().map { PlayerRowInfo(it) })

                // Fetch details for each player
                // This part is async and iterative, making single Success state tricky
                // For now, let's assume we show initial list then update with details
                // Or, ideally, wait for all details before Success.
                // Let's try to wait for all details.
                val updatedPlayersInfo = players.map { playerInfo ->
                    // This map should ideally run these requests in parallel if possible using async/await
                    // For simplicity, keeping it sequential as per original logic.
                    val questInfo = requests.getRewardsInfo(playerInfo.name)
                    playerInfo.apply {
                        chests = questInfo.questRewardInfo.chestEarned
                        chestUrl = questInfo.questRewardInfo.getChestUrl()
                        timeLeft = questInfo.questRewardInfo.getFormattedEndDateShort()
                    }
                }
                // players list is already updated in place by reference.

                _state.value = LoginViewState.Success(
                    players = players.toList(), // Send a copy
                    onDeletePlayer = ::onDelete,
                    onAddPlayer = ::onAddPlayer
                )
            } catch (e: Exception) {
                // The coroutineExceptionHandler should catch this, but as a safeguard:
                _state.value = LoginViewState.Error(e.message ?: "Failed to load player data.")
                throw e // Re-throw for CEH to handle _isRefreshing.value = false
            } finally {
                // If try completes without error, CEH won't set _isRefreshing.
                // If an error occurs and CEH handles it, this will also run.
                _isRefreshing.value = false
            }
        }
    }

    // updateSuccessState is removed as its logic is integrated into onRefresh's try block.

    private fun onDelete(player: String) {
        cache.deletePlayerFromList(player)
        onRefresh() // Call the new refresh logic
    }

    private fun onAddPlayer(player: String) {
        // This function has its own loading and error states, separate from main screen refresh
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true // Indicate general activity
            _state.value = LoginViewState.Loading() // Main screen goes to loading
            try {
                if (requests.getPlayerDetails(player).name == null) { // gson serializes missing as null
                    _state.value = LoginViewState.CouldNotFindPlayerError(
                        player = player,
                        onAddPlayer = ::onAddPlayer, // Pass the function reference
                        onClickBack = ::onRefresh // Pass the function reference
                    )
                } else {
                    cache.writePlayerToList(player)
                    session.setCurrentPlayer(player) // Set current player in session
                    onRefresh() // Refresh the main list
                }
            } catch (e: Exception) {
                _state.value = LoginViewState.CouldNotFindPlayerError( // Or a more general error
                    player = player,
                    onAddPlayer = ::onAddPlayer,
                    onClickBack = ::onRefresh
                )
                // If we reach here, it's likely not a CEH-triggering exception, but an application logic error.
                // The CEH will handle _isRefreshing = false if 'throw e' is called.
                // If we don't re-throw, we must set _isRefreshing = false.
                // For consistency, let's assume CEH handles it if 'e' is thrown.
                throw e
            } finally {
                // This finally block in onAddPlayer might be redundant if CEH is always triggered by exceptions.
                // However, if an error occurs that doesn't go through CEH, this is a safeguard.
                // Let's ensure _isRefreshing is false.
                _isRefreshing.value = false 
            }
        }
    }
}