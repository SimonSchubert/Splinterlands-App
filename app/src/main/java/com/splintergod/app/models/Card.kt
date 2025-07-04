package com.splintergod.app.models

import com.google.gson.annotations.SerializedName
import com.splintergod.app.R
import com.splintergod.app.assetUrl

data class Card(
    @SerializedName("card_detail_id") val cardDetailId: String,
    val edition: Int,
    val gold: Boolean = false,
    val level: Int,
    var mana: Int = 0,
    var health: Int = 0,
    var speed: Int = 0,
    var magic: Int = 0,
    var melee: Int = 0,
    var range: Int = 0,
    var name: String = "",
    var imageUrl: String = "",
    var goldLevels: List<Int> = emptyList(),
    var regularLevels: List<Int> = emptyList()
) {

    fun setStats(cardDetail: CardDetail) {
        imageUrl = getImageUrl(cardDetail, level)
        name = cardDetail.name
        val statsIndex = level - 1
        mana = cardDetail.stats.mana?.getOrNull(statsIndex) ?: 0
        health = cardDetail.stats.health?.getOrNull(statsIndex) ?: 0
        speed = cardDetail.stats.speed?.getOrNull(statsIndex) ?: 0
        magic = cardDetail.stats.magic?.getOrNull(statsIndex) ?: 0
        melee = cardDetail.stats.attack?.getOrNull(statsIndex) ?: 0
        range = cardDetail.stats.ranged?.getOrNull(statsIndex) ?: 0
    }

    fun getImageUrl(cardDetail: CardDetail, level: Int): String {
        val editionPath = when (edition) {
            13 -> "rebellion"
            10 -> "soulbound"
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
        println("${assetUrl}cards_by_level/$editionPath/${cardDetail.name}_lv${level}${isGoldPath}.png")
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

    fun getFoilId(): String {
        return if (gold) {
            "Gold"
        } else {
            "Regular"
        }
    }
}