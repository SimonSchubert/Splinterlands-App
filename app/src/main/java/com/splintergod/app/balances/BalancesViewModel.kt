package com.splintergod.app.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import com.splintergod.app.Session
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BalancesViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<BalancesViewState>(BalancesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = BalancesViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = BalancesViewState.Loading { onRefresh() }
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = cache.getBalances(session.player)
            )
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = requests.getBalances(session.player)
            )
        }
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _state.value = BalancesViewState.Loading { onRefresh() }
            _state.value = BalancesViewState.Success(
                onRefresh = { onRefresh() },
                balances = requests.getBalances(session.player)
            )
        }
    }
}