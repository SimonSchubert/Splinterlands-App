package com.example.splinterlandstest

import android.content.Context
import com.example.splinterlandstest.models.BalancesResponse
import com.example.splinterlandstest.models.Battle
import com.example.splinterlandstest.models.BattleHistoryResponse
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import com.example.splinterlandstest.models.CollectionResponse
import com.example.splinterlandstest.models.GameSettings
import com.example.splinterlandstest.models.PlayerDetailsResponse
import com.example.splinterlandstest.models.RewardsInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.File
import java.lang.reflect.Type

class Cache(val context: Context) {

    fun getSelectedPlayerName(): String {
        val file = File(context.filesDir, "player_name.json")
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun writeSelectedPlayerName(player: String) {
        context.openFileOutput("player_name.json", Context.MODE_PRIVATE).use {
            it.write(player.toByteArray())
        }
    }

    fun getPlayerList(): List<String> {
        val file = File(context.filesDir, "players.json")
        return if (file.exists()) {
            try {
                JSONArray(file.readText()).toStringList()
            } catch (ignore: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writePlayerToList(player: String) {
        val players = getPlayerList().toMutableList()
        if (!players.contains(player)) {
            players.add(player)
            write(
                fileName = "players.json",
                data = players.joinToString(prefix = "[", postfix = "]")
            )
        }
    }

    fun deletePlayerFromList(player: String) {
        val players = getPlayerList().toMutableList()
        players.remove(player)
        write(
            fileName = "players.json",
            data = players.joinToString(prefix = "[", postfix = "]")
        )
    }

    fun getBalances(player: String): List<BalancesResponse> {
        return get<List<BalancesResponse>?>(
            fileName = "balances_${player}.json",
            type = object : TypeToken<List<BalancesResponse>>() {}.type
        )?.filterBalances() ?: emptyList()
    }

    fun write(fileName: String, data: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    fun <T> get(fileName: String, type: Class<T>): T? {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                return Gson().fromJson(file.readText(), type)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return null
    }

    fun <T> get(fileName: String, type: Type): T? {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                return Gson().fromJson(file.readText(), type)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return null
    }

    fun getCollection(player: String): List<Card> {
        return get(
            fileName = "collection_${player}.json",
            type = CollectionResponse::class.java
        )?.cards?.distinctBy { it.card_detail_id } ?: emptyList()
    }

    fun getCardDetails(): List<CardDetail> {
        return get(
            fileName = "card_details.json",
            type = object : TypeToken<List<CardDetail>>() {}.type
        ) ?: emptyList()
    }

    fun getBattleHistory(player: String): List<Battle> {
        val battles = mutableListOf<Battle>()

        val wildBattles: BattleHistoryResponse? = get(
            fileName = "battles_${player}_wild.json",
            type = BattleHistoryResponse::class.java
        )
        wildBattles?.battles?.let {
            battles.addAll(it)
        }

        val modernBattles: BattleHistoryResponse? = get(
            fileName = "battles_${player}_modern.json",
            type = BattleHistoryResponse::class.java
        )
        modernBattles?.battles?.let {
            battles.addAll(it)
        }

        return battles.sortedByDescending { it.created_date }
    }

    fun getPlayerDetails(player: String): PlayerDetailsResponse? {
        return get(
            fileName = "details_${player}.json",
            type = PlayerDetailsResponse::class.java
        )
    }

    fun getRewardsInfo(player: String): RewardsInfo? {
        return get(
            fileName = "rewards_info_${player}.json",
            type = RewardsInfo::class.java
        )
    }

    fun getSettings(): GameSettings? {
        return get(
            fileName = "game_settings.json",
            type = GameSettings::class.java
        )
    }

    fun JSONArray.toStringList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.optString(i, ""))
        }
        return list.filter { it.isNotBlank() }
    }

}