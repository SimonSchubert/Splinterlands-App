package com.example.splinterlandstest.rewards

import com.example.splinterlandstest.models.Reward

sealed class RewardsViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : RewardsViewState(isRefreshing = true)
    data class Success(override val onRefresh: () -> Unit, val rewards: List<Reward>) : RewardsViewState(isRefreshing = false)
    data class Error(override val onRefresh: () -> Unit) : RewardsViewState(isRefreshing = true)
}