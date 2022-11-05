package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class RewardsInfo(val quest_reward_info: QuestRewardInfo, val season_reward_info: SeasonRewardInfo)