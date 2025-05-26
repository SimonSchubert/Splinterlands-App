package com.splintergod.app.rewards

import com.splintergod.app.models.RewardGroup

sealed class RewardsViewState {

    data class Loading() : RewardsViewState()
    data class Success(
        val rewardsGroups: List<RewardGroup>
    ) : RewardsViewState()

    data class Error(val message: String? = null) : RewardsViewState()
}