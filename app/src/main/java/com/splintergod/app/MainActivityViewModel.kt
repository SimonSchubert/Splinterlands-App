package com.splintergod.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun init() {
        playerName = cache.getSelectedPlayerName()
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            cache.getSettings()?.let {
                assetUrl = it.assetUrl
            }
            val gameSettings = requests.getSettings()
            assetUrl = gameSettings.assetUrl
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