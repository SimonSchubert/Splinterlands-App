package com.splintergod.app.models

import com.google.gson.annotations.SerializedName
import kotlin.math.floor
import kotlin.math.min

data class PlayerDetails(
    @SerializedName("last_reward_block") val lastRewardBlock: Long,
    @SerializedName("capture_rate") val captureRate: Float,
    val rank: String,
    val rating: Int,
    @SerializedName("modern_rating") val modernRating: Int,
    val wins: Int,
    val name: String
) {
    fun getCurrentCaptureRate(settings: GameSettings): Int {
        val value = (captureRate + (settings.lastBlock - lastRewardBlock) / 1200f)
        return min(floor(value), 50f).toInt()
    }
}