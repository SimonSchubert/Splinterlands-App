package com.splintergod.app.rewards

import com.splintergod.app.models.RewardGroup

sealed class RewardsViewState(open val isRefreshing: Boolean) {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : RewardsViewState(isRefreshing = true)
    data class Success(
        override val onRefresh: () -> Unit,
        override val isRefreshing: Boolean,
        val rewardsGroups: List<RewardGroup>
    ) : RewardsViewState(isRefreshing = isRefreshing)

    data class Error(override val onRefresh: () -> Unit) : RewardsViewState(isRefreshing = true)
}