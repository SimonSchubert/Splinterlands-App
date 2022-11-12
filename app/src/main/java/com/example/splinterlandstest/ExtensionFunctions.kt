package com.example.splinterlandstest

import android.widget.ImageView
import com.example.splinterlandstest.models.Balances
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import com.squareup.picasso.Picasso
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

fun Picasso.loadCard(imageView: ImageView, card: Card, cardDetail: CardDetail) {
    this
        .load(card.getImageUrl(cardDetail))
        .placeholder(card.getPlaceholderDrawable())
        .fit()
        .into(imageView)
}

fun String.getRulesetImageUrl(): String {
    val namePath = this.lowercase().replace("&", "").replace("  ", " ").replace(" ", "-")
    return "${assetUrl}website/icons/rulesets/new/img_combat-rule_${namePath}_150.png"
}