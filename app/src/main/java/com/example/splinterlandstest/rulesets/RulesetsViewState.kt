package com.example.splinterlandstest.rulesets

import com.example.splinterlandstest.models.Ruleset

sealed class RulesetsViewState {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : RulesetsViewState()
    data class Success(override val onRefresh: () -> Unit, val rulesets: List<Ruleset>) : RulesetsViewState()
    data class Error(override val onRefresh: () -> Unit) : RulesetsViewState()
}