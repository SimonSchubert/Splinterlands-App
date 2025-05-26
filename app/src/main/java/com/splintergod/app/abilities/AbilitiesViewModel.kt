package com.splintergod.app.abilities

import androidx.lifecycle.ViewModel
import com.splintergod.app.Cache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AbilitiesViewModel(val cache: Cache) : ViewModel() {

    private val _state =
        MutableStateFlow<AbilitiesViewState>(AbilitiesViewState.Loading())
    val state: StateFlow<AbilitiesViewState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        refreshAbilities()
    }

    fun refreshAbilities() {
        // This ViewModel doesn't have async calls, so isRefreshing might be very brief.
        // For consistency with async ViewModels, we use a similar structure.
        _isRefreshing.value = true
        _state.value = AbilitiesViewState.Loading()
        try {
            val abilities = cache.getAbilities()
            // Assuming getAbilities() itself doesn't throw for "empty" or "error" scenarios,
            // and instead returns a list (empty or not).
            // If an actual exception occurs during cache.getAbilities(), it will be caught.
            _state.value = AbilitiesViewState.Success(abilities = abilities)
        } catch (e: Exception) {
            // Handle any unexpected errors during the synchronous call
            _state.value = AbilitiesViewState.Error(e.message ?: "Failed to load abilities.")
            // Optionally log the exception e
        } finally {
            _isRefreshing.value = false
        }
    }

    // Keep the old onRefresh for a moment if state objects still refer to it,
    // but the goal is to remove it from ViewState.
    // private fun onRefresh() {
    //     refreshAbilities()
    // }
}