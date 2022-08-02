package com.example.splinterlandstest.rewards

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RewardsFragmentViewModel : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    val rewards: MutableLiveData<List<Requests.Reward>> = MutableLiveData()
    val cardDetails: MutableLiveData<List<Requests.CardDetail>> = MutableLiveData()

    fun loadRewards(context: Context, player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            cardDetails.postValue(cache.getCardDetails(context))
            if (cardDetails.value?.isEmpty() == true) {
                cardDetails.postValue(requests.getCardDetails(context))
            }
            rewards.postValue(requests.getRecentRewards(player))
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}