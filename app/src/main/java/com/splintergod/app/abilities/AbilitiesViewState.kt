package com.splintergod.app.abilities

import com.splintergod.app.models.Ability

sealed class AbilitiesViewState {

    data class Loading() : AbilitiesViewState()
    data class Success(val abilities: List<Ability>) :
        AbilitiesViewState()

    data class Error(val message: String? = null) : AbilitiesViewState()
}