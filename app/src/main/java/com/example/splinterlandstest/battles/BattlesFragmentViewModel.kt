package com.example.splinterlandstest.battles

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BattlesFragmentViewModel : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    val battles: MutableLiveData<List<Requests.Battle>> = MutableLiveData()
    val playerDetails: MutableLiveData<Requests.PlayerDetailsResponse> = MutableLiveData()
    val playerQuest: MutableLiveData<Requests.QuestResponse?> = MutableLiveData()
    val cardDetails: MutableLiveData<List<Requests.CardDetail>> = MutableLiveData()

    fun loadBattles(context: Context, player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            battles.postValue(cache.getBattleHistory(context, player))
            playerDetails.postValue(cache.getPlayerDetails(context, player))
            playerQuest.postValue(cache.getPlayerQuest(context, player).firstOrNull())
            battles.postValue(requests.getBattleHistory(context, player))
            playerDetails.postValue(requests.getPlayerDetails(context, player))
            playerQuest.postValue(requests.getPlayerQuest(context, player).firstOrNull())
            cardDetails.postValue(requests.getCardDetails(context))
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}