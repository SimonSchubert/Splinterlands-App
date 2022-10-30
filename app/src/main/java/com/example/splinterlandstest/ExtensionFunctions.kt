package com.example.splinterlandstest

import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

fun List<Requests.BalancesResponse>.filterBalances(): List<Requests.BalancesResponse> {
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

fun Picasso.loadCard(imageView: ImageView, card: Requests.Card, cardDetail: Requests.CardDetail) {
    this
        .load(card.getImageUrl(cardDetail))
        .placeholder(card.getPlaceholderDrawable())
        .fit()
        .into(imageView)
}

fun Picasso.loadCard(imageView: ImageView, card: Requests.Card, path: String) {
    this
        .load(path)
        .placeholder(card.getPlaceholderDrawable())
        .fit()
        .into(imageView)
}