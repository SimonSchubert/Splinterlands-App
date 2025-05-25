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
import kotlinx.coroutines.flow.StateFlow

class BalancesViewModel(val session: Session, val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<BalancesViewState>(BalancesViewState.Loading)
    val state: StateFlow<BalancesViewState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = BalancesViewState.Error
        _isRefreshing.value = false // Ensure refreshing is stopped on error
    }

    init {
        refreshBalances()
    }

    fun refreshBalances() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true
            _state.value = BalancesViewState.Loading
            try {
                // The problem description states loadRewards() loads balances.
                // The original loadRewards() first showed cached, then fetched.
                // The original onRefresh() only fetched.
                // Consolidating to fetch directly for refreshBalances().
                val balances = requests.getBalances(session.player)
                if (balances.isNotEmpty()) {
                    _state.value = BalancesViewState.Success(balances = balances)
                } else {
                    // Assuming empty balances is a valid success state, not an error.
                    _state.value = BalancesViewState.Success(balances = emptyList())
                }
            } catch (e: Exception) {
                // Handled by coroutineExceptionHandler
                _state.value = BalancesViewState.Error // Explicitly set error state
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}