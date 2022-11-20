package com.example.splinterlandstest.rulesets

import com.example.splinterlandstest.models.Ruleset

sealed class RulesetsViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : RulesetsViewState(true)
    data class Success(override val onRefresh: () -> Unit, val rulesets: List<Ruleset>) : RulesetsViewState(false)
    data class Error(override val onRefresh: () -> Unit) : RulesetsViewState(false)
}