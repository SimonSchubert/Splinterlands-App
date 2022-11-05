package com.example.splinterlandstest.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<LoginViewState>(LoginViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val players = mutableListOf<PlayerRowInfo>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    data class PlayerRowInfo(val name: String, var chests: Int = 2, var timeLeft: String = "", var chestUrl: String = "")

    fun loadPlayerData() {
        players.clear()
        players.addAll(cache.getPlayerList().map { PlayerRowInfo(it) })

        updateSuccessState()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            players.forEach { player ->
                val questInfo = requests.getRewardsInfo(player.name)
                player.chests = questInfo.quest_reward_info.chest_earned
                player.chestUrl = questInfo.quest_reward_info.getChestUrl()
                player.timeLeft = questInfo.quest_reward_info.getFormattedEndDateShort()
                updateSuccessState()
            }
        }
    }

    private fun updateSuccessState() {
        _state.value = LoginViewState.Success(players = players,
            onDeletePlayer = {},
            onAddPlayer = { onAddPlayer(it) },
            onRefresh = { onRefresh() }
        )
    }

    private fun onRefresh() {
        loadPlayerData()
    }

    fun onDelete(player: String) {
        players.find { it.name == player }?.let {
            players.remove(it)
        }
    }

    private fun onAddPlayer(player: String) {
        _state.value = LoginViewState.Loading { onRefresh() }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            delay(1000)

            cache.writePlayerToList(player)

            loadPlayerData()
        }
    }
}

class LoginFragmentViewModelFactory(val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(cache, requests) as T
    }
}