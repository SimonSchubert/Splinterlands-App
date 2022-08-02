package com.example.splinterlandstest.balances

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BalancesFragmentViewModel : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    val balances: MutableLiveData<List<Requests.BalancesResponse>> = MutableLiveData()

    fun loadBalances(context: Context, player: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            balances.postValue(cache.getBalances(context, player))
            balances.postValue(requests.getBalances(context, player))
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}