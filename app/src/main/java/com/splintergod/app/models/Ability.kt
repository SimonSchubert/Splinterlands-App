package com.splintergod.app.models

import com.splintergod.app.assetUrl

data class Ability(val name: String, val desc: String) {
    fun getImageUrl(): String {
        return "${assetUrl}website/abilities/ability_${name.lowercase().replace(" ", "-")}.png"
    }
}