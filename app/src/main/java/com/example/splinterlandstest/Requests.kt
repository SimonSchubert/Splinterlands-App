package com.example.splinterlandstest

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds


val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

class Requests {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val cache = Cache()

    private val endpoint = "https://api2.splinterlands.com"


    @Serializable
    data class Card(val card_detail_id: String, val edition: Int, val gold: Boolean = false) {
        fun getPath(cardDetail: CardDetail): String {
            val editionPath = when (edition) {
                7 -> "cards_chaos"
                6 -> "cards_gladiator"
                4, 5 -> "cards_untamed"
                2 -> "cards_v2.2"
                else -> "cards_beta"
            }
            val fileEnding = getFileEnding(cardDetail)
            val isGoldPath = if (gold) {
                "_gold"
            } else {
                ""
            }
            return "https://d36mxiodymuqjm.cloudfront.net/$editionPath/${cardDetail.name}${isGoldPath}.$fileEnding"
        }

        fun getPlaceholderDrawable(): Int {
            return when (edition) {
                6 -> R.drawable.card6
                5 -> R.drawable.card5
                4 -> R.drawable.card4
                3 -> R.drawable.card3
                2 -> R.drawable.card2
                1 -> R.drawable.card1
                else -> R.drawable.card7
            }
        }

        private fun getFileEnding(cardDetail: CardDetail): String {
            return if (edition == 7 || edition == 3 && cardDetail.tier == 7) {
                "jpg"
            } else {
                "png"
            }
        }
    }

    @Serializable
    data class CardDetail(val id: String, val name: String, val tier: Int? = -1, val rarity: Int)

    @Serializable
    data class CollectionResponse(val player: String, val cards: List<Card>)

    @Serializable
    data class BattleDetailsTeam(val player: String, val summoner: Card, val monsters: List<Card>)

    @Serializable
    data class BattleDetails(val team1: BattleDetailsTeam?, val team2: BattleDetailsTeam?)

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
        val battle_queue_id_1: String
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
                .map { "https://d36mxiodymuqjm.cloudfront.net/website/icons/rulesets/new/img_combat-rule_${it}_150.png" }
        }

        fun getTimeAgo(): String {
            val milliseconds = System.currentTimeMillis() - (simpleDateFormat.parse(created_date)?.time ?: 0L)
            return "${milliseconds.absoluteValue.div(1000L).seconds}".split(" ").first()
        }
    }

    @Serializable
    data class BattleHistoryResponse(val player: String, val battles: List<Battle>)

    @Serializable
    data class PlayerDetailsResponse(
        val capture_rate: Int,
        val rank: String,
        val rating: Int,
        val wins: Int,
        val name: String
    )

    data class QuestInfo(val chests: Int, val nextChestRshares: Long, val requiredRshares: Long, val chestTier: Int) {
        fun getChestUrl(): String {
            val league = when (chestTier) {
                1 -> "silver"
                2 -> "gold"
                3 -> "diamond"
                4 -> "champion"
                else -> "bronze"
            }
            return "https://d36mxiodymuqjm.cloudfront.net/website/ui_elements/updated_rewards/img_chest_modern_$league.png"
        }
    }

    @Serializable
    data class QuestResponse(
        val chest_tier: Int,
        val rshares: Long,
        val created_date: String
    ) {
        fun getCurrentQuestInfo(): QuestInfo {
            val config = Cache().getQuestConfig(chest_tier)

            var chests = -1
            var totalRshares = 0.0
            var nextChest = 0.0
            while (rshares >= totalRshares.roundToInt()) {
                chests++
                nextChest = (config.base * config.multiplier.pow(chests)).toDouble()
                totalRshares += nextChest
            }

            val requiredRshares = totalRshares - rshares
            if (chests > 30) {
                chests = 30
            }
            return QuestInfo(chests, nextChest.toLong(), requiredRshares.toLong(), chest_tier)
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

    suspend fun getBalances(context: Context, player: String): List<BalancesResponse> {
        val response: HttpResponse = client.get("$endpoint/players/balances?username=$player")
        cache.writeBalances(context, response.bodyAsText(), player)
        return (Gson().fromJson(
            response.bodyAsText(),
            object : TypeToken<List<BalancesResponse>>() {}.type
        ) as List<BalancesResponse>).filterBalances()
    }

    suspend fun getCollection(context: Context, player: String): List<Card> {
        val response: HttpResponse = client.get("$endpoint/cards/collection/$player")
        cache.writeCollection(context, response.bodyAsText(), player)
        return Gson().fromJson(
            response.bodyAsText(),
            CollectionResponse::class.java
        ).cards.distinctBy { it.card_detail_id }
    }

    suspend fun getCardDetails(context: Context): List<CardDetail> {
        val response: HttpResponse = client.get("$endpoint/cards/get_details")
        cache.writeCardDetails(context, response.bodyAsText())
        return Gson().fromJson(
            response.bodyAsText(),
            object : TypeToken<List<CardDetail>>() {}.type
        )
    }

    suspend fun getBattleHistory(context: Context, player: String): List<Battle> {
        val responseWild: HttpResponse =
            client.get("$endpoint/battle/history2?player=$player&username=$player")
        cache.writeBattleHistory(context, responseWild.bodyAsText(), player, "wild")

        val responseModern: HttpResponse =
            client.get("$endpoint/battle/history2?player=$player&username=$player&format=modern")
        cache.writeBattleHistory(context, responseModern.bodyAsText(), player, "modern")

        val battles = Gson().fromJson(responseWild.bodyAsText(), BattleHistoryResponse::class.java).battles +
                Gson().fromJson(responseModern.bodyAsText(), BattleHistoryResponse::class.java).battles
        return battles.sortedByDescending { it.created_date }
    }

    suspend fun getPlayerDetails(context: Context, player: String): PlayerDetailsResponse {
        val response: HttpResponse =
            client.get("$endpoint/players/details?name=$player")
        cache.writePlayerDetails(context, response.bodyAsText(), player)
        return Gson().fromJson(response.bodyAsText(), PlayerDetailsResponse::class.java)
    }

    suspend fun getPlayerQuest(context: Context, player: String): List<QuestResponse> {
        val response: HttpResponse =
            client.get("$endpoint/players/quests?username=$player")
        cache.writePlayerQuest(context, response.bodyAsText(), player)
        return Gson().fromJson(
            response.bodyAsText(),
            object : TypeToken<List<QuestResponse>>() {}.type
        )
    }

    sealed class Reward
    data class DecReward(val quantity: Int) : Reward()
    data class CreditsReward(val quantity: Int) : Reward()
    data class MeritsReward(val quantity: Int) : Reward()
    data class GoldPotionReward(val quantity: Int) : Reward()
    data class LegendaryPotionReward(val quantity: Int) : Reward()
    object PackReward : Reward()
    data class CardReward(val cardId: Int, val isGold: Boolean) : Reward()

    private suspend fun getLatestClaimRewardTransactionId(player: String): String {
        val jsonBody =
            "{\"id\":4,\"jsonrpc\":\"2.0\",\"method\":\"condenser_api.get_account_history\",\"params\":[\"$player\",-1,300]}"
        val request: HttpResponse = client.post("https://anyx.io/") {
            header("accept", "*/*")
            setBody(TextContent(jsonBody, ContentType.Application.Json))
        }.body()
        val json = JSONObject(request.bodyAsText())
        json.getJSONArray("result").toArrayList().reversed().forEach {
            val obj = it.getJSONObject(1)
            val data = obj.getJSONArray("op").getJSONObject(1)
            val dataId = data.optString("id", "")
            if (dataId == "sm_claim_reward") {
                return obj.getString("trx_id")
            }
        }
        return ""
    }

    suspend fun getRecentRewards(player: String): List<Reward> {
        val transactionId = getLatestClaimRewardTransactionId(player)
        if (transactionId.isNotBlank()) {
            val request2: HttpResponse =
                client.get("$endpoint/transactions/lookup?trx_id=$transactionId") {
                    header("accept", "*/*")
                }.body()
            val json3 = JSONObject(request2.bodyAsText())
            if (json3.optString("error", "") == "") {
                val rewards = mutableListOf<Reward>()
                val json2 = JSONObject(json3.getJSONObject("trx_info").getString("result"))
                json2.getJSONArray("rewards").toObjectList().forEach {
                    when (it.getString("type")) {
                        "potion" -> {
                            if (it.getString("potion_type") == "gold") {
                                rewards.add(GoldPotionReward(it.getInt("quantity")))
                            } else {
                                rewards.add(LegendaryPotionReward(it.getInt("quantity")))
                            }
                        }

                        "reward_card" -> {
                            val cardDetailId = it.getJSONObject("card").getInt("card_detail_id")
                            val isGold = it.getJSONObject("card").getBoolean("gold")
                            rewards.add(CardReward(cardDetailId, isGold))
                        }

                        "credits" -> {
                            rewards.add(CreditsReward(it.getInt("quantity")))
                        }

                        "merits" -> {
                            rewards.add(MeritsReward(it.getInt("quantity")))
                        }

                        "dec" -> {
                            rewards.add(DecReward(it.getInt("quantity")))
                        }

                        "pack" -> {
                            rewards.add(PackReward)
                        }
                    }
                }
                return rewards.toList()
            }
        }
        return emptyList()
    }

}