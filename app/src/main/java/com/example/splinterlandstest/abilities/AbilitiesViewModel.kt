package com.example.splinterlandstest.abilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splinterlandstest.Cache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AbilitiesViewModel(val cache: Cache) : ViewModel() {

    private val _state = MutableStateFlow<AbilitiesViewState>(AbilitiesViewState.Loading { onRefresh() })
    val state = _state.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        val abilities = cache.getAbilities()
        _state.value = AbilitiesViewState.Success(
            onRefresh = { onRefresh() },
            abilities = abilities
        )
    }
}

class AbilitiesViewModelFactory(val cache: Cache) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AbilitiesViewModel(cache) as T
    }
}