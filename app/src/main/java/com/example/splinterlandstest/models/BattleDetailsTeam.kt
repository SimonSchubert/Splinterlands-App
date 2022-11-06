package com.example.splinterlandstest.models

import kotlinx.serialization.Serializable

@Serializable
data class BattleDetailsTeam(val player: String, val summoner: Card, val monsters: List<Card>) {

    fun getCardUrls(cardDetails: List<CardDetail>): List<CardFoilUrl> {
        return mutableListOf<CardFoilUrl>().apply {
            add(CardFoilUrl(getCardImageUrl(summoner, cardDetails), summoner.gold))
            monsters.forEach {
                add(CardFoilUrl(getCardImageUrl(it, cardDetails), it.gold))
            }
        }
    }

    private fun getCardImageUrl(card: Card, cardDetails: List<CardDetail>): String {
        val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }
        return if (cardDetail != null) {
            card.getImageUrl(cardDetail)
        } else {
            ""
        }
    }
}

data class CardFoilUrl(val url: String, val isGold: Boolean)