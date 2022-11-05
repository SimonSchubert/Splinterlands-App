package com.example.splinterlandstest

import com.example.splinterlandstest.models.BalancesResponse
import com.example.splinterlandstest.models.Battle
import com.example.splinterlandstest.models.BattleHistoryResponse
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import com.example.splinterlandstest.models.CardReward
import com.example.splinterlandstest.models.CollectionResponse
import com.example.splinterlandstest.models.CreditsReward
import com.example.splinterlandstest.models.DecReward
import com.example.splinterlandstest.models.GameSettings
import com.example.splinterlandstest.models.GoldPotionReward
import com.example.splinterlandstest.models.LegendaryPotionReward
import com.example.splinterlandstest.models.MeritsReward
import com.example.splinterlandstest.models.PackReward
import com.example.splinterlandstest.models.PlayerDetailsResponse
import com.example.splinterlandstest.models.Reward
import com.example.splinterlandstest.models.RewardsInfo
import com.example.splinterlandstest.models.SPSReward
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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
var assetUrl = ""

class Requests(val cache: Cache) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    // private val cache = Cache(MainApplication.appContext)

    private val endpoint = "https://api2.splinterlands.com"

    suspend fun getSettings(): GameSettings {
        val response: HttpResponse = client.get("$endpoint/settings")
        cache.write("game_settings.json", response.bodyAsText())
        return Gson().fromJson(
            response.bodyAsText(),
            GameSettings::class.java
        )
    }

    suspend fun getBalances(player: String): List<BalancesResponse> {
        val response: HttpResponse = client.get("$endpoint/players/balances?username=$player")
        cache.write("balances_${player}.json", response.bodyAsText())
        return (Gson().fromJson(
            response.bodyAsText(),
            object : TypeToken<List<BalancesResponse>>() {}.type
        ) as List<BalancesResponse>).filterBalances()
    }

    suspend fun getCollection(player: String): List<Card> {
        val response: HttpResponse = client.get("$endpoint/cards/collection/$player")
        cache.write("collection_${player}.json", response.bodyAsText())
        return Gson().fromJson(
            response.bodyAsText(),
            CollectionResponse::class.java
        ).cards.distinctBy { it.card_detail_id }
    }

    suspend fun getCardDetails(): List<CardDetail> {
        val response: HttpResponse = client.get("$endpoint/cards/get_details")
        cache.write("card_details.json", response.bodyAsText())
        return Gson().fromJson(
            response.bodyAsText(),
            object : TypeToken<List<CardDetail>>() {}.type
        )
    }

    suspend fun getBattleHistory(player: String): List<Battle> {
        val responseWild: HttpResponse =
            client.get("$endpoint/battle/history2?player=$player&username=$player")
        cache.write("battles_${player}_wild.json", responseWild.bodyAsText())

        val responseModern: HttpResponse =
            client.get("$endpoint/battle/history2?player=$player&username=$player&format=modern")
        cache.write("battles_${player}_modern.json", responseModern.bodyAsText())

        val battles = Gson().fromJson(responseWild.bodyAsText(), BattleHistoryResponse::class.java).battles +
                Gson().fromJson(responseModern.bodyAsText(), BattleHistoryResponse::class.java).battles
        return battles.sortedByDescending { it.created_date }
    }

    suspend fun getPlayerDetails(player: String): PlayerDetailsResponse {
        val response: HttpResponse =
            client.get("$endpoint/players/details?name=$player")
        cache.write("details_${player}.json", response.bodyAsText())
        return Gson().fromJson(response.bodyAsText(), PlayerDetailsResponse::class.java)
    }

    suspend fun getRewardsInfo(player: String): RewardsInfo {
        val response: HttpResponse =
            client.get("$endpoint/players/current_rewards?username=$player")
        cache.write("rewards_info_${player}.json", response.bodyAsText())
        return Gson().fromJson(response.bodyAsText(), RewardsInfo::class.java)
    }


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
            val request: HttpResponse =
                client.get("$endpoint/transactions/lookup?trx_id=$transactionId") {
                    header("accept", "*/*")
                }.body()
            val json = JSONObject(request.bodyAsText())
            if (json.optString("error", "") == "") {
                val rewards = mutableListOf<Reward>()
                val resultJson = JSONObject(json.getJSONObject("trx_info").getString("result"))
                resultJson.getJSONArray("rewards").toObjectList().forEach {
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

                        "sps" -> {
                            rewards.add(SPSReward(it.getDouble("quantity").toFloat()))
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