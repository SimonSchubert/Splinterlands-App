package com.example.splinterlandstest.rulesets

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

            var rulesets = cache.getSettings()?.battles?.rulesets

            if (rulesets.isNullOrEmpty() || forceRefresh) {
                rulesets = requests.getSettings().battles.rulesets
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

class RulesetsViewModelFactory(val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RulesetsViewModel(cache, requests) as T
    }
}