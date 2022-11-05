package com.example.splinterlandstest.balances

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

class BalancesViewModel(val player: String, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<BalancesViewState>(BalancesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = BalancesViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = cache.getBalances(player)
            )
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = requests.getBalances(player)
            )
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = BalancesViewState.Loading { onRefresh() }
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = requests.getBalances(player)
            )
        }
    }
}

class BalancesViewModelFactory(val player: String, val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BalancesViewModel(player, cache, requests) as T
    }
}