package com.splintergod.app.abilities

import androidx.lifecycle.ViewModel
import com.splintergod.app.Cache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AbilitiesViewModel(val cache: Cache) : ViewModel() {

    private val _state =
        MutableStateFlow<AbilitiesViewState>(AbilitiesViewState.Loading(onRefresh = ::refreshAbilities))
    val state: StateFlow<AbilitiesViewState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        refreshAbilities()
    }

    fun refreshAbilities() {
        // This ViewModel doesn't have async calls, so isRefreshing might be very brief.
        // If it had async calls, it would be:
        // viewModelScope.launch {
        //     _isRefreshing.value = true
        //     _state.value = AbilitiesViewState.Loading // Optional: also set data state to Loading
        //     try {
        //         val abilities = cache.getAbilities() // Or async call
        //         _state.value = AbilitiesViewState.Success(abilities = abilities)
        //     } catch (e: Exception) {
        //         _state.value = AbilitiesViewState.Error
        //     } finally {
        //         _isRefreshing.value = false
        //     }
        // }

        // Simplified for synchronous cache call
        _isRefreshing.value = true // For pull-to-refresh UI consistency
        _state.value = AbilitiesViewState.Loading(onRefresh = ::refreshAbilities)
        val abilities = cache.getAbilities()
        if (abilities.isNotEmpty()) { // Assuming empty list is not an error but could be success with empty data
            _state.value =
                AbilitiesViewState.Success(onRefresh = ::refreshAbilities, abilities = abilities)
        } else {
            // Decide if empty abilities list is an error or just empty success state
            // For now, let it be success with empty list. If error:
            // _state.value = AbilitiesViewState.Error
            _state.value =
                AbilitiesViewState.Success(onRefresh = ::refreshAbilities, abilities = abilities)
        }
        _isRefreshing.value = false
    }

    // Keep the old onRefresh for a moment if state objects still refer to it,
    // but the goal is to remove it from ViewState.
    // private fun onRefresh() {
    //     refreshAbilities()
    // }
}