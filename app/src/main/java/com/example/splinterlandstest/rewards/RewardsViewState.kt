package com.example.splinterlandstest.rewards

import com.example.splinterlandstest.models.Reward

sealed class RewardsViewState {
    abstract val onRefresh: () -> Unit

    data class Loading(override val onRefresh: () -> Unit) : RewardsViewState()
    data class Success(override val onRefresh: () -> Unit, val rewards: List<Reward>) : RewardsViewState()
    data class Error(override val onRefresh: () -> Unit) : RewardsViewState()
}