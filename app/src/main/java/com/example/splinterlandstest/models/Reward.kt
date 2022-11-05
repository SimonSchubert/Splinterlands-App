package com.example.splinterlandstest.models

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

data class CardReward(val cardId: Int, val isGold: Boolean, var name: String = "", var url: String = "") :
    Reward() {
    override fun getTitle(): String {
        return name
    }
}