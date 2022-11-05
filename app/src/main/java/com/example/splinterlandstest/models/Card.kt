package com.example.splinterlandstest.models

import com.example.splinterlandstest.R
import com.example.splinterlandstest.assetUrl
import kotlinx.serialization.Serializable

@Serializable
data class Card(val card_detail_id: String, val edition: Int, val gold: Boolean = false) {
    fun getImageUrl(cardDetail: CardDetail): String {
        val editionPath = when (edition) {
            8 -> "cards_riftwatchers"
            7 -> "cards_chaos"
            6 -> "cards_gladiator"
            4, 5 -> "cards_untamed"
            2 -> "cards_v2.2"
            else -> "cards_beta"
        }
        val fileEnding = getFileEnding(cardDetail)
        val isGoldPath = if (gold) {
            "_gold"
        } else {
            ""
        }
        return "${assetUrl}$editionPath/${cardDetail.name}${isGoldPath}.$fileEnding"
    }

    fun getPlaceholderDrawable(): Int {
        return when (edition) {
            8 -> R.drawable.card8
            7 -> R.drawable.card7
            6 -> R.drawable.card6
            5 -> R.drawable.card5
            4 -> R.drawable.card4
            3 -> R.drawable.card3
            2 -> R.drawable.card2
            1 -> R.drawable.card1
            else -> R.drawable.card7
        }
    }

    private fun getFileEnding(cardDetail: CardDetail): String {
        return if (edition == 8 || edition == 7 || edition == 3 && cardDetail.tier == 7) {
            "jpg"
        } else {
            "png"
        }
    }
}