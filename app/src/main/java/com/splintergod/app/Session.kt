package com.splintergod.app

class Session(val cache: Cache) {

    var player: String = ""
    var currentCardDetailId: String = ""
    var currentCardDetailLevel: Int = 0

    init {
        player = cache.getSelectedPlayerName()
    }

    fun setCurrentPlayer(playerName: String) {
        player = playerName
        cache.writeSelectedPlayerName(playerName)
    }

    fun logout() {
        player = ""
        cache.writeSelectedPlayerName("")
    }
}