package com.example.splinterlandstest.login

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragmentViewModel(context: Context) : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    private var _players = mutableStateListOf<PlayerRowInfo>()
    val players: List<PlayerRowInfo>
        get() = _players

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    data class PlayerRowInfo(val name: String, var chests: Int = 2, var timeLeft: String = "", var chestUrl: String = "")

    init {
        val playerList = cache.getPlayerList(context)
        _players.addAll(playerList.map { PlayerRowInfo(it) })

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            playerList.forEachIndexed { index, player ->
                val questInfo = requests.getRewardsInfo(context, player)
                players.find { it.name == player }?.let {
                    it.chests = questInfo.quest_reward_info.chest_earned
                    it.chestUrl = questInfo.quest_reward_info.getChestUrl()
                    it.timeLeft = questInfo.quest_reward_info.getFormattedEndDateShort()
                    _players.remove(it)
                    _players.add(index, it)
                }
            }
        }
    }

    fun onDelete(player: String) {
        players.find { it.name == player }?.let {
            _players.remove(it)
        }
    }

    fun onAdd(player: String) {
        _players.add(PlayerRowInfo(player))
    }
}

class LoginFragmentViewModelFactory(val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(context) as T
    }
}