package com.splintergod.app.models

import com.google.gson.annotations.SerializedName


data class RewardsInfo(
    @SerializedName("quest_reward_info") val questRewardInfo: QuestRewardInfo,
    @SerializedName("season_reward_info") val seasonRewardInfo: SeasonRewardInfo
)