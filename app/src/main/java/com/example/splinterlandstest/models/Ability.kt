package com.example.splinterlandstest.models

import com.example.splinterlandstest.assetUrl

data class Ability(val name: String, val desc: String) {
    fun getImageUrl(): String {
        return "${assetUrl}website/abilities/ability_${name.lowercase().replace(" ", "-")}.png"
    }
}