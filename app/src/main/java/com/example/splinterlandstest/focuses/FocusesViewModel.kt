package com.example.splinterlandstest.focuses

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

class FocusesViewModel(val cache: Cache, val requests: Requests) : ViewModel() {

    private val _state = MutableStateFlow<FocusesViewState>(FocusesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = FocusesViewState.Error { onRefresh() }
    }

    fun loadRewards() {
        onRefresh()
    }

    private fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _state.value = FocusesViewState.Loading { onRefresh() }

            var focuses = cache.getSettings()?.dailyQuests

            if (focuses == null || focuses.isEmpty()) {
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

class FocusesViewModelFactory(val cache: Cache, val requests: Requests) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FocusesViewModel(cache, requests) as T
    }
}