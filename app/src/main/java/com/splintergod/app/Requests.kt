package com.splintergod.app

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.splintergod.app.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
var assetUrl = ""

class Requests(val cache: Cache) {

    private val gson = GsonBuilder()
        .create()

    private val client = HttpClient(CIO)

    private val endpoint = "https://api2.splinterlands.com"

    suspend fun getSettings(): GameSettings {
        val response: HttpResponse = client.get("$endpoint/settings")
        cache.write("game_settings.json", response.bodyAsText())
        return gson.fromJson(
            response.bodyAsText(),
            GameSettings::class.java
        )
    }

    suspend fun getBalances(player: String): List<Balances> {
        val response: HttpResponse = client.get("$endpoint/players/balances?username=$player")
        cache.write("balances_${player}.json", response.bodyAsText())
        return (gson.fromJson(
            response.bodyAsText(),
            object : TypeToken<List<Balances>>() {}.type
        ) as List<Balances>).filterBalances()
    }

    suspend fun getCollection(player: String): List<Card> {
        val response: HttpResponse = client.get("$endpoint/cards/collection/$player")
        cache.write("collection_${player}.json", response.bodyAsText())
        return gson.fromJson(
            response.bodyAsText(),
            CollectionResponse::class.java
        ).cards.groupCards()
    }

    suspend fun getCardDetails(): List<CardDetail> {
        val response: HttpResponse = client.get("$endpoint/cards/get_details")
        val jsonArray = JSONArray(response.bodyAsText())

        // replace with custom deserializer
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i).getJSONObject("stats")

            val fields = listOf(
                "mana",
                "health",
                "speed",
                "attack",
                "ranged",
                "magic",
                "armor",
                "abilities"
            )
            fields.forEach {
                item.put(it, item.optJSONArray(it))
            }
            if (item.optJSONArray("abilities")?.optJSONArray(0) == null) {
                item.put("abilities", null)
            }
        }

        cache.write("card_details.json", jsonArray.toString())
        return gson.fromJson(
            jsonArray.toString(),
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

        val battles = gson.fromJson(responseWild.bodyAsText(), BattleHistory::class.java).battles +
                gson.fromJson(responseModern.bodyAsText(), BattleHistory::class.java).battles
        return battles.sortedByDescending { it.createdDate }
    }

    suspend fun getPlayerDetails(player: String): PlayerDetails {
        val response: HttpResponse =
            client.get("$endpoint/players/details?name=$player")
        cache.write("details_${player}.json", response.bodyAsText())
        return gson.fromJson(response.bodyAsText(), PlayerDetails::class.java)
    }

    suspend fun getRewardsInfo(player: String): RewardsInfo {
        val response: HttpResponse =
            client.get("$endpoint/players/current_rewards?username=$player")
        cache.write("rewards_info_${player}.json", response.bodyAsText())
        return gson.fromJson(response.bodyAsText(), RewardsInfo::class.java)
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

    suspend fun getRecentRewards(player: String): RewardGroup? {
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

                val date = json.getJSONObject("trx_info").getString("created_date")

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
                            val cardJson = it.getJSONObject("card")
                            val cardDetailId = cardJson.getInt("card_detail_id")
                            val isGold = cardJson.getBoolean("gold")
                            val edition = cardJson.optInt("edition", 3)
                            repeat(it.optInt("quantity", 1)) {
                                rewards.add(CardReward(cardDetailId, isGold, edition))
                            }
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
                return RewardGroup(date, rewards.toList())
            }
        }
        return null
    }

}