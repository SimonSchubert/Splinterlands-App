package com.example.splinterlandstest

import Requests
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class Cache {

    fun getPlayerName(context: Context): String {
        val file = File(context.filesDir, "player")
        return if (file.exists()) {
            File(context.filesDir, "player").readText()
        } else {
            ""
        }
    }

    fun writePlayerName(context: Context, player: String) {
        context.openFileOutput("player", Context.MODE_PRIVATE).use {
            it.write(player.toByteArray())
        }
    }

    fun getBalances(context: Context, player: String): List<Requests.BalancesResponse> {
        val file = File(context.filesDir, "balances_$player")
        return if (file.exists()) {
            (Gson().fromJson(
                file.readText(),
                object : TypeToken<List<Requests.BalancesResponse>>() {}.type
            ) as List<Requests.BalancesResponse>).filterBalances()
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
            Gson().fromJson(
                file.readText(),
                Requests.CollectionResponse::class.java
            ).cards.distinctBy { it.card_detail_id }
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
            return Gson().fromJson(
                file.readText(),
                object : TypeToken<List<Requests.CardDetail>>() {}.type
            )
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
            Gson().fromJson(file.readText(), Requests.BattleHistoryResponse::class.java).battles
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
            Gson().fromJson(file.readText(), Requests.PlayerDetailsResponse::class.java)
        } else {
            Requests.PlayerDetailsResponse(0, "", 0, 0, "")
        }
    }

    fun writePlayerDetails(context: Context, response: String, player: String) {
        context.openFileOutput("details_$player", Context.MODE_PRIVATE).use {
            it.write(response.toByteArray())
        }
    }
}