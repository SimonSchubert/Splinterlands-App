package com.example.splinterlandstest.models

import com.example.splinterlandstest.R
import kotlinx.serialization.Serializable

@Serializable
data class BalancesResponse(val player: String, var token: String, var balance: Float) {
    fun getDrawableResource(): Int {
        return when (token) {
            "DEC" -> R.drawable.dec
            "CREDITS" -> R.drawable.credits
            "SPS" -> R.drawable.sps
            "MERITS" -> R.drawable.mertis
            "GOLD" -> R.drawable.gold
            "LEGENDARY" -> R.drawable.legendary
            "GLADIUS" -> R.drawable.gladius
            "DICE" -> R.drawable.dice
            "UNTAMED" -> R.drawable.untamed
            "ORB" -> R.drawable.orb
            "ALPHA" -> R.drawable.alpha
            "BETA" -> R.drawable.beta
            "CHAOS" -> R.drawable.chaos
            "NIGHTMARE" -> R.drawable.nightmare
            "RIFT" -> R.drawable.rift
            "PLOT" -> R.drawable.plot
            "VOUCHER" -> R.drawable.voucher
            "LICENSE" -> R.drawable.license
            "TOTEMC" -> R.drawable.totemc
            "TOTEMR" -> R.drawable.totemr
            "TOTEME" -> R.drawable.toteme
            "TOTEML" -> R.drawable.toteml
            "TRACT" -> R.drawable.tract
            "REGION" -> R.drawable.region
            else -> R.drawable.ic_launcher_background
        }
    }
}