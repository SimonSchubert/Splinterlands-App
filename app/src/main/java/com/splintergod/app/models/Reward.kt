package com.splintergod.app.models

import com.splintergod.app.simpleDateFormat
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class RewardGroup(val date: String, val rewards: List<Reward>) {
    var player: String = ""

    fun getFormattedDateShort(): String {
        val seconds = getSecondsAgo()
        return "$seconds".split(" ").first() + " ago"
    }

    fun getSecondsAgo(): Duration {
        val timestamp = (simpleDateFormat.parse(date)?.time?.div(1_000)
            ?: 0L)
        return (System.currentTimeMillis().div(1000L) - timestamp).seconds
    }
}

sealed class Reward {
    abstract fun getTitle(): String
}

data class DecReward(val quantity: Int) : Reward() {
    override fun getTitle(): String {
        return "$quantity DEC"
    }
}

data class CreditsReward(val quantity: Int) : Reward() {
    override fun getTitle(): String {
        return "$quantity CREDITS"
    }
}

data class MeritsReward(val quantity: Int) : Reward() {
    override fun getTitle(): String {
        return "$quantity MERITS"
    }
}

data class SPSReward(val quantity: Float) : Reward() {
    override fun getTitle(): String {
        return "$quantity SPS"
    }
}

data class GoldPotionReward(val quantity: Int) : Reward() {
    override fun getTitle(): String {
        return "$quantity"
    }
}

data class LegendaryPotionReward(val quantity: Int) : Reward() {
    override fun getTitle(): String {
        return "$quantity"
    }
}

object PackReward : Reward() {
    override fun getTitle(): String {
        return "PACK"
    }
}

data class RewardDate(val date: String) : Reward() {
    override fun getTitle(): String {
        return date
    }
}

data class CardReward(
    val cardId: Int,
    val isGold: Boolean,
    val edition: Int,
    var name: String = "",
    var url: String = ""
) :
    Reward() {
    override fun getTitle(): String {
        return name
    }
}