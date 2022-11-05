package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl
import kotlinx.serialization.Serializable

@Serializable
data class SeasonRewardInfo(
    val chest_tier: Int,
    val chest_earned: Int,
    val rshares: Long
) {

    fun getChestUrl(): String {
        val league = when (chest_tier) {
            1 -> "silver"
            2 -> "gold"
            3 -> "diamond"
            4 -> "champion"
            else -> "bronze"
        }
        return "${assetUrl}website/ui_elements/updated_rewards/img_chest_modern_$league.png"
    }
}