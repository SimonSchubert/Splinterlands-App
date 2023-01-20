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

    private val _state = MutableStateFlow<RulesetsViewState>(RulesetsViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = RulesetsViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        onRefresh(false)
    }

    private fun onRefresh(forceRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = RulesetsViewState.Loading { onRefresh() }

            var rulesets = cache.getSettings()?.battles?.rulesets?.filter { it.active }

            if (rulesets.isNullOrEmpty() || forceRefresh) {
                rulesets = requests.getSettings().battles.rulesets.filter { it.active }
            }

            if (rulesets.isNotEmpty()) {
                _state.value = RulesetsViewState.Success(
                    onRefresh = { onRefresh() },
                    rulesets = rulesets
                )
            } else {
                _state.value = RulesetsViewState.Error { onRefresh() }
            }
        }
    }
}