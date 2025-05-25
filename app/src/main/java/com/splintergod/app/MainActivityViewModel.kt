package com.splintergod.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    var isInitialized = false

    fun setPlayer(playerName: String) {
        session.setCurrentPlayer(playerName)
        init()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun init() {
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
        return session.player.isNotEmpty()
    }

    fun getPlayerName(): String = session.player

    fun logout() {
        session.logout()
    }
}