package com.example.splinterlandstest.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {

    private val requests = Requests()

    val quests: MutableLiveData<HashMap<String, Requests.QuestResponse>> = MutableLiveData()

    fun loadUsers(context: Context, players: List<String>) {
        viewModelScope.launch {
            quests.value = hashMapOf()
            players.forEach { player ->
                val questInfo = requests.getPlayerQuest(context, player).firstOrNull()
                if (questInfo != null) {
                    quests.value!![player] = questInfo
                    quests.postValue(quests.value)
                }
            }
        }
    }
}