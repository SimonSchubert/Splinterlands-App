package com.example.splinterlandstest.models

import com.example.splinterlandstest.getRulesetImageUrl
import com.example.splinterlandstest.simpleDateFormat
import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

data class Battle(
    @SerializedName("created_date") val createdDate: String,
    val winner: String,
    @SerializedName("player_1") val player1: String,
    @SerializedName("player_1_rating_final") val player1RatingFinal: Int,
    @SerializedName("player_2") val player2: String,
    @SerializedName("player_2_rating_final") val player2RatingFinal: Int,
    val ruleset: String,
    val inactive: String,
    @SerializedName("mana_cap") val manaCap: Int,
    val details: BattleDetails,
    @SerializedName("battle_queue_id_1") val battleQueueId1: String,
    @SerializedName("match_type") val matchType: String,
    val format: String
) {
    fun getOpponent(player: String): String {
        return if (player1.uppercase() == player.uppercase()) {
            player2.uppercase()
        } else {
            player1.uppercase()
        }
    }

    fun getOwnRating(player: String): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return if (player1.uppercase() == player.uppercase()) {
            numberFormat.format(player1RatingFinal)
        } else {
            numberFormat.format(player2RatingFinal)
        }
    }

    fun getOpponentRating(player: String): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return if (player1.uppercase() == player.uppercase()) {
            numberFormat.format(player2RatingFinal)
        } else {
            numberFormat.format(player1RatingFinal)
        }
    }

    fun getOwnDetail(player: String): BattleDetailsTeam? {
        return if (player1.uppercase() == player.uppercase()) {
            details.team1
        } else {
            details.team2
        }
    }

    fun getOpponentDetail(player: String): BattleDetailsTeam? {
        return if (player1.uppercase() == player.uppercase()) {
            details.team2
        } else {
            details.team1
        }
    }

    fun isWin(player: String): Boolean {
        return winner == player
    }

    fun getRulesetImageUrls(): List<String> {
        return ruleset.split("|").map { it.getRulesetImageUrl() }
    }

    fun getTimeAgo(): String {
        val milliseconds = System.currentTimeMillis() - (simpleDateFormat.parse(createdDate)?.time ?: 0L)
        return "${milliseconds.absoluteValue.div(1000L).seconds}".split(" ").first()
    }

    fun getType(): String {
        return if (matchType == "Ranked") {
            if (format == "modern") {
                "Modern"
            } else {
                "Wild"
            }
        } else if (details.isBrawl) {
            "Brawl"
        } else {
            matchType
        }
    }
}