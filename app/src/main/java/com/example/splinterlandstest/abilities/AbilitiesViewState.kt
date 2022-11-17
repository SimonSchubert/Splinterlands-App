package com.example.splinterlandstest.abilities

import com.example.splinterlandstest.models.Ability

sealed class AbilitiesViewState {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : AbilitiesViewState()
    data class Success(override val onRefresh: () -> Unit, val abilities: List<Ability>) : AbilitiesViewState()
    data class Error(override val onRefresh: () -> Unit) : AbilitiesViewState()
}