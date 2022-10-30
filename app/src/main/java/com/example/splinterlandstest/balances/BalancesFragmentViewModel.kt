package com.example.splinterlandstest.balances

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BalancesFragmentViewModel(context: Context, player: String) : ViewModel() {

    private val requests = Requests()
    private val cache = Cache()

    private var _state = MutableStateFlow<List<Requests.BalancesResponse>>(emptyList())
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = cache.getBalances(context, player)
            _state.value = requests.getBalances(context, player)
        }
    }
}

class BasicGroupModelFactory(val context: Context, val player: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BalancesFragmentViewModel(context, player) as T
    }
}