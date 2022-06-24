package com.example.splinterlandstest

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.File

class Cache {

    data class QuestConfig(val base: Int, val multiplier: Float)

    fun getQuestConfig(rank: Int): QuestConfig {
        return when (rank) {
            1 -> QuestConfig(5000, 1.13f)
            2 -> QuestConfig(18000, 1.09f)
            3 -> QuestConfig(43000, 1.062f)
            4 -> QuestConfig(90000, 1.038f)
            else -> QuestConfig(300, 1.2f)
        }
    }

    fun getPlayerName(context: Context): String {
        val file = File(context.filesDir, "player")
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun writePlayerName(context: Context, player: String) {
        context.openFileOutput("player", Context.MODE_PRIVATE).use {
            it.write(player.toByteArray())
        }
    }

    fun getPlayerList(context: Context): List<String> {
        val file = File(context.filesDir, "players")
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

    fun writePlayerToList(context: Context, player: String) {
        val players = getPlayerList(context).toMutableList()
        if (!players.contains(player)) {
            players.add(player)
            context.openFileOutput("players", Context.MODE_PRIVATE).use {
                it.write(players.joinToString(prefix = "[", postfix = "]").toByteArray())
            }
        }
    }

    fun deletePlayerFromList(context: Context, player: String) {
        val players = getPlayerList(context).toMutableList()
        players.remove(player)
        context.openFileOutput("players", Context.MODE_PRIVATE).use {
            it.write(players.joinToString(prefix = "[", postfix = "]").toByteArray())
        }
    }

    fun getBalances(context: Context, player: String): List<Requests.BalancesResponse> {
        val file = File(context.filesDir, "balances_$player")
        return if (file.exists()) {
            try {
                (Gson().fromJson(
                    file.readText(),
                    object : TypeToken<List<Requests.BalancesResponse>>() {}.type
                ) as List<Requests.BalancesResponse>).filterBalances()
            } catch (exception: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeBalances(context: Context, response: String, player: String) {
        context.openFileOutput("balances_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun getCollection(context: Context, player: String): List<Requests.Card> {
        val file = File(context.filesDir, "collection_$player")
        return if (file.exists()) {
            try {
                Gson().fromJson(
                    file.readText(),
                    Requests.CollectionResponse::class.java
                ).cards.distinctBy { it.card_detail_id }
            } catch (ignore: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeCollection(context: Context, response: String, player: String) {
        context.openFileOutput("collection_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun getCardDetails(context: Context): List<Requests.CardDetail> {
        val file = File(context.filesDir, "card_details")
        return if (file.exists()) {
            try {
                return Gson().fromJson(
                    file.readText(),
                    object : TypeToken<List<Requests.CardDetail>>() {}.type
                )
            } catch (exception: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeCardDetails(context: Context, response: String) {
        context.openFileOutput("card_details", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun getBattleHistory(context: Context, player: String): List<Requests.Battle> {
        val file = File(context.filesDir, "battles_$player")
        return if (file.exists()) {
            try {
                Gson().fromJson(file.readText(), Requests.BattleHistoryResponse::class.java).battles
            } catch (exception: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeBattleHistory(context: Context, response: String, player: String) {
        context.openFileOutput("battles_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun getPlayerDetails(context: Context, player: String): Requests.PlayerDetailsResponse {
        val file = File(context.filesDir, "details_$player")
        return if (file.exists()) {
            try {
                Gson().fromJson(file.readText(), Requests.PlayerDetailsResponse::class.java)
            } catch (exception: Exception) {
                Requests.PlayerDetailsResponse(0, "", 0, 0, "")
            }
        } else {
            Requests.PlayerDetailsResponse(0, "", 0, 0, "")
        }
    }

    fun writePlayerDetails(context: Context, response: String, player: String) {
        context.openFileOutput("details_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun getPlayerQuest(context: Context, player: String): List<Requests.QuestResponse> {
        val file = File(context.filesDir, "quest_$player")
        return if (file.exists()) {
            try {
                return Gson().fromJson(
                    file.readText(),
                    object : TypeToken<List<Requests.QuestResponse>>() {}.type
                )
            } catch (exception: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writePlayerQuest(context: Context, response: String, player: String) {
        context.openFileOutput("quest_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }

    fun JSONArray.toStringList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.optString(i, ""))
        }
        return list.filter { it.isNotBlank() }
    }
}