package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl
import com.google.gson.annotations.SerializedName

data class SeasonRewardInfo(
    @SerializedName("chest_tier") val chestTier: Int,
    @SerializedName("chest_earned") val chestEarned: Int,
    val rshares: Long
) {

    fun getChestUrl(): String {
        val league = when (chestTier) {
            1 -> "silver"
            2 -> "gold"
            3 -> "diamond"
            4 -> "champion"
            else -> "bronze"
        }
        return "${assetUrl}website/ui_elements/updated_rewards/img_chest_modern_$league.png"
    }
}