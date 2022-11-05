package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl
import com.example.splinterlandstest.simpleDateFormat
import kotlinx.serialization.Serializable
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Battle(
    val created_date: String,
    val winner: String,
    val player_1: String,
    val player_1_rating_final: Int,
    val player_2: String,
    val player_2_rating_final: Int,
    val ruleset: String,
    val inactive: String,
    val mana_cap: Int,
    val details: BattleDetails,
    val battle_queue_id_1: String,
    val match_type: String,
    val format: String
) {
    fun getOpponent(player: String): String {
        return if (player_1 == player) {
            player_2
        } else {
            player_1
        }
    }

    fun getOwnRating(player: String): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return if (player_1 == player) {
            numberFormat.format(player_1_rating_final)
        } else {
            numberFormat.format(player_2_rating_final)
        }
    }

    fun getOpponentRating(player: String): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return if (player_2 == player) {
            numberFormat.format(player_2_rating_final)
        } else {
            numberFormat.format(player_1_rating_final)
        }
    }

    fun getOwnDetail(player: String): BattleDetailsTeam? {
        return if (player_1 == player) {
            details.team1
        } else {
            details.team2
        }
    }

    fun getOpponentDetail(opponent: String): BattleDetailsTeam? {
        return if (player_1 == opponent) {
            details.team1
        } else {
            details.team2
        }
    }

    fun isWin(player: String): Boolean {
        return winner == player
    }

    fun getRulesetImagePaths(): List<String> {
        return ruleset.split("|").map { it.lowercase().replace("&", "").replace("  ", " ").replace(" ", "-") }
            .map { "${assetUrl}website/icons/rulesets/new/img_combat-rule_${it}_150.png" }
    }

    fun getTimeAgo(): String {
        val milliseconds = System.currentTimeMillis() - (simpleDateFormat.parse(created_date)?.time ?: 0L)
        return "${milliseconds.absoluteValue.div(1000L).seconds}".split(" ").first()
    }
}