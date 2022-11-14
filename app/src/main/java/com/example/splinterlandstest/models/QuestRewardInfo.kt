package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl
import com.example.splinterlandstest.simpleDateFormat
import com.google.gson.annotations.SerializedName
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

data class QuestRewardInfo(
    @SerializedName("created_date") val createdDate: String,
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

    fun getEndTimestamp(): Long {
        return (simpleDateFormat.parse(createdDate)?.time?.div(1_000)
            ?: 0L) + 1.days.inWholeSeconds
    }

    fun getFormattedEndDate(): String {
        val milliseconds = System.currentTimeMillis() - (simpleDateFormat.parse(createdDate)?.time
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