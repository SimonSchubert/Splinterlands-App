package com.splintergod.app.models

data class CardDetail(
    val id: String,
    val name: String,
    val tier: Int? = -1,
    val rarity: Int,
    val color: String,
    val secondary_color: String,
    val type: String,
    val stats: CardDetailStats,
    val editions: String,
)

data class CardDetailStats(
    var mana: List<Int>?,
    var health: List<Int>?,
    var speed: List<Int>?,
    var attack: List<Int>?,
    var ranged: List<Int>?,
    var magic: List<Int>?,
    var armor: List<Int>?,
    var abilities: List<List<String>>?
)