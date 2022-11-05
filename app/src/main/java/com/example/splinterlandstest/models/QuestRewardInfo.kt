package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl
import com.example.splinterlandstest.simpleDateFormat
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Serializable
data class QuestRewardInfo(
    val chest_tier: Int,
    val chest_earned: Int,
    val created_date: String,
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

    fun getFormattedEndDate(): String {
        val milliseconds = System.currentTimeMillis() - (simpleDateFormat.parse(created_date)?.time
            ?: 0L) - 1.days.inWholeMilliseconds
        return if (milliseconds > 0) {
            "Claim reward"
        } else {
            "${milliseconds.absoluteValue.div(1000L).seconds}"
        }
    }

    fun getFormattedEndDateShort(): String {
        val date = getFormattedEndDate()
        return date.split(" ").first()
    }
}