package com.example.splinterlandstest

import com.example.splinterlandstest.models.Balances
import org.json.JSONArray
import org.json.JSONObject

fun List<Balances>.filterBalances(): List<Balances> {
    val sps = this.firstOrNull { it.token == "SPS" }
    this.firstOrNull { it.token == "SPSP" }?.let {
        if (sps != null) {
            sps.balance += it.balance
        }
    }
    return this.sortedByDescending { it.balance }.filter { it.balance.toInt() > 0 }
        .filter { it.getDrawableResource() != R.drawable.ic_launcher_background }
}

fun JSONArray.toArrayList(): List<JSONArray> {
    val list = mutableListOf<JSONArray>()
    for (i in 0 until this.length()) {
        list.add(this.getJSONArray(i))
    }
    return list
}

fun JSONArray.toObjectList(): List<JSONObject> {
    val list = mutableListOf<JSONObject>()
    for (i in 0 until this.length()) {
        list.add(this.getJSONObject(i))
    }
    return list
}

fun String.getRulesetImageUrl(): String {
    val namePath = this.lowercase().replace("&", "").replace("  ", " ").replace(" ", "-")
    return "${assetUrl}website/icons/rulesets/new/img_combat-rule_${namePath}_150.png"
}