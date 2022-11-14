package com.example.splinterlandstest.models

import com.example.splinterlandstest.R
import com.example.splinterlandstest.assetUrl
import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("card_detail_id") val cardDetailId: String,
    val edition: Int,
    val gold: Boolean = false,
    val level: Int
) {

    fun getImageUrl(cardDetail: CardDetail): String {
        val editionPath = when (edition) {
            8 -> "rift"
            7 -> "chaos"
            6 -> "gladius"
            5 -> "dice"
            4 -> "untamed"
            3 -> "reward"
            2 -> "promo"
            1 -> "beta"
            0 -> "alpha"
            else -> ""
        }
        val isGoldPath = if (gold) {
            "_gold"
        } else {
            ""
        }
        return "${assetUrl}cards_by_level/$editionPath/${cardDetail.name}_lv${level}${isGoldPath}.png"
    }

    fun getPlaceholderDrawable(): Int {
        return when (edition) {
            8 -> R.drawable.card_back_rift
            7 -> R.drawable.card_back_chaos
            6 -> R.drawable.card_back_gladius
            5 -> R.drawable.card_back_untamed
            4 -> R.drawable.card_back_reward
            3 -> R.drawable.card_back_promo
            2 -> R.drawable.card_back_beta
            1 -> R.drawable.card_back_alpha
            else -> R.drawable.card_back_chaos
        }
    }
}