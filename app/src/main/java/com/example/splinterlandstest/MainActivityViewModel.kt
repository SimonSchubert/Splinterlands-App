package com.example.splinterlandstest

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    val loginStatus: MutableLiveData<Boolean> = MutableLiveData()

    var playerName = ""

    private val requests = Requests()
    private val cache = Cache()
    var isInitialized = false

    fun setPlayer(context: Context, playerName: String) {
        this.playerName = playerName
        Cache().writePlayerName(context, playerName)
        Cache().writePlayerToList(context, playerName)
        loginStatus.value = true
    }

    fun deletePlayer(context: Context, playerName: String) {
        Cache().deletePlayerFromList(context, playerName)
    }

    fun init(context: Context) {
        playerName = Cache().getPlayerName(context)
        viewModelScope.launch {
            cache.getSettings(context).let {
                assetUrl = it.asset_url
            }
            val gameSettings = requests.getSettings(context)
            assetUrl = gameSettings.asset_url
        }
        isInitialized = true
    }

    fun isLoggedIn(): Boolean {
        return playerName != ""
    }

    fun logout(context: Context) {
        playerName = ""
        Cache().writePlayerName(context, "")
        loginStatus.value = false
    }
}