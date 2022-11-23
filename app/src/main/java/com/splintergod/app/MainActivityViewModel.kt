package com.splintergod.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    val loginStatus: MutableLiveData<Boolean> = MutableLiveData()

    var isInitialized = false

    fun setPlayer(playerName: String) {
        session.setCurrentPlayer(playerName)
        loginStatus.value = true
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
        return loginStatus.value == true
    }

    fun logout() {
        session.logout()
        loginStatus.value = false
    }
}