package com.splintergod.app.rulesets

import com.splintergod.app.models.Ruleset

sealed class RulesetsViewState {

    data class Loading() : RulesetsViewState()
    data class Success(val rulesets: List<Ruleset>) :
        RulesetsViewState()

    data class Error(val message: String? = null) : RulesetsViewState()
}