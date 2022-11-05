package com.example.splinterlandstest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    val loginStatus: MutableLiveData<Boolean> = MutableLiveData()

    var playerName = ""

    var isInitialized = false

    fun setPlayer(playerName: String) {
        this.playerName = playerName
        cache.writeSelectedPlayerName(playerName)
        loginStatus.value = true
    }

    fun init() {
        playerName = cache.getSelectedPlayerName()
        viewModelScope.launch {
            cache.getSettings()?.let {
                assetUrl = it.asset_url
            }
            val gameSettings = requests.getSettings()
            assetUrl = gameSettings.asset_url
        }
        isInitialized = true
    }

    fun isLoggedIn(): Boolean {
        return playerName != ""
    }

    fun logout() {
        playerName = ""
        cache.writeSelectedPlayerName("")
        loginStatus.value = false
    }
}

class MainActivityViewModelFactory(val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(cache, requests) as T
    }
}