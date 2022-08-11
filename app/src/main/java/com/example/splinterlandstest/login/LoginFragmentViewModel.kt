package com.example.splinterlandstest.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {

    private val requests = Requests()

    val quests: MutableLiveData<HashMap<String, Requests.RewardsInfo>> = MutableLiveData()

    fun loadUsers(context: Context, players: List<String>) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            quests.postValue(hashMapOf())
            players.forEach { player ->
                val questInfo = requests.getRewardsInfo(context, player)
                quests.value!![player] = questInfo
                quests.postValue(quests.value)
            }
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}