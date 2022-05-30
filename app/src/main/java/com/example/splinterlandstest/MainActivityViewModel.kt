package com.example.splinterlandstest

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val loginStatus: MutableLiveData<Boolean> = MutableLiveData()

    var playerName = ""

    fun setPlayer(context: Context, playerName: String) {
        this.playerName = playerName
        Cache().writePlayerName(context, playerName)
        loginStatus.value = true
    }

    fun init(context: Context) {
        playerName = Cache().getPlayerName(context)
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