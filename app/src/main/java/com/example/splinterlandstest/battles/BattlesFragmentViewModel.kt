package com.example.splinterlandstest.battles

import Requests
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import kotlinx.coroutines.launch

class BattlesFragmentViewModel : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    val battles: MutableLiveData<List<Requests.Battle>> = MutableLiveData()
    val playerDetails: MutableLiveData<Requests.PlayerDetailsResponse> = MutableLiveData()

    fun loadBattles(context: Context, player: String) {
        viewModelScope.launch {
            battles.value = cache.getBattleHistory(context, player)
            playerDetails.value = cache.getPlayerDetails(context, player)
            battles.value = requests.getBattleHistory(context, player)
            playerDetails.value = requests.getPlayerDetails(context, player)
        }
    }
}