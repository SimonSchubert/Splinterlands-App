package com.splintergod.app.rulesets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RulesetsViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state =
        MutableStateFlow<RulesetsViewState>(RulesetsViewState.Loading())
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = RulesetsViewState.Error(throwable.message ?: "Failed to load rulesets.")
        _isRefreshing.value = false // Ensure refreshing is stopped on error
    }

    fun loadRewards() {
        onRefresh(false)
    }

    private fun onRefresh(forceRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _isRefreshing.value = true
            _state.value = RulesetsViewState.Loading()
            try {
                var rulesets = cache.getSettings()?.battles?.rulesets?.filter { it.active }

                if (rulesets.isNullOrEmpty() || forceRefresh) {
                    rulesets = requests.getSettings().battles.rulesets.filter { it.active }
                }

                if (rulesets.isNotEmpty()) {
                    _state.value = RulesetsViewState.Success(
                        rulesets = rulesets
                    )
                } else {
                _state.value = RulesetsViewState.Error("No rulesets found.")
                }
            } catch (e: Exception) {
                // This will be caught by coroutineExceptionHandler if it's a coroutine exception
            _state.value = RulesetsViewState.Error(e.message ?: "Failed to process rulesets.")
                // Log e
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}