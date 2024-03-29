package com.splintergod.app.focuses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splintergod.app.Cache
import com.splintergod.app.Requests
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusesViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<FocusesViewState>(FocusesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = FocusesViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        onRefresh(false)
    }

    private fun onRefresh(forceRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = FocusesViewState.Loading { onRefresh() }

            var focuses = cache.getSettings()?.dailyQuests

            if (focuses.isNullOrEmpty() || forceRefresh) {
                focuses = requests.getSettings().dailyQuests
            }

            if (focuses.isNotEmpty()) {
                _state.value = FocusesViewState.Success(
                    onRefresh = { onRefresh() },
                    focuses = focuses
                )
            } else {
                _state.value = FocusesViewState.Error { onRefresh() }
            }
        }
    }
}