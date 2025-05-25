package com.splintergod.app.models

import com.splintergod.app.R


data class Balances(val player: String, var token: String, var balance: Float) {
    fun getDrawableResource(): Int {
        return when (token) {
            "VOUCHER" -> R.drawable.asset_voucher
            "GLINT" -> R.drawable.asset_glint
            "DEC" -> R.drawable.asset_dec
            "DEC-B" -> R.drawable.asset_dec_b
            "CREDITS" -> R.drawable.asset_credits
            "SPS" -> R.drawable.asset_sps
            "MERITS" -> R.drawable.asset_merits
            "GOLD" -> R.drawable.asset_potion_gold
            "LEGENDARY" -> R.drawable.asset_potion_legendary
            "GLADIUS" -> R.drawable.asset_pack_gladius
            "DICE" -> R.drawable.asset_pack_dice
            "UNTAMED" -> R.drawable.asset_pack_untamed
            "ORB" -> R.drawable.asset_pack_orb
            "ALPHA" -> R.drawable.asset_pack_alpha
            "BETA" -> R.drawable.asset_pack_beta
            "CHAOS" -> R.drawable.asset_pack_chaos
            "NIGHTMARE" -> R.drawable.asset_pack_nightmare
            "RIFT" -> R.drawable.asset_pack_rift
            "PLOT" -> R.drawable.asset_land_plot
            "VOUCHER-TOTAL" -> R.drawable.asset_voucher
            "LICENSE" -> R.drawable.asset_node_license
            "TOTEMC" -> R.drawable.asset_totem_common
            "TOTEMR" -> R.drawable.asset_totem_rare
            "TOTEME" -> R.drawable.asset_totem_epic
            "TOTEML" -> R.drawable.asset_totem_legendary
            "TRACT" -> R.drawable.asset_land_tract
            "REGION" -> R.drawable.asset_plot_region
            else -> R.drawable.ic_launcher_background
        }
    }
}