package com.splintergod.app.abilities

import androidx.lifecycle.ViewModel
import com.splintergod.app.Cache
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