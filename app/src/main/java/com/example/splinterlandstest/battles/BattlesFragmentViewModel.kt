package com.example.splinterlandstest.battles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.Battle
import com.example.splinterlandstest.models.CardDetail
import com.example.splinterlandstest.models.PlayerDetailsResponse
import com.example.splinterlandstest.models.RewardsInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BattlesFragmentViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    val battles: MutableLiveData<List<Battle>> = MutableLiveData()
    val playerDetails: MutableLiveData<PlayerDetailsResponse> = MutableLiveData()
    val rewardsInfo: MutableLiveData<RewardsInfo?> = MutableLiveData()
    val cardDetails: MutableLiveData<List<CardDetail>> = MutableLiveData()

    fun loadBattles(player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            battles.postValue(cache.getBattleHistory(player))
            playerDetails.postValue(cache.getPlayerDetails(player))
            rewardsInfo.postValue(cache.getRewardsInfo(player))
            battles.postValue(requests.getBattleHistory(player))
            playerDetails.postValue(requests.getPlayerDetails(player))
            rewardsInfo.postValue(requests.getRewardsInfo(player))
            cardDetails.postValue(requests.getCardDetails())
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}