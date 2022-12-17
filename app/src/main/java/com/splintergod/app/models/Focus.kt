package com.splintergod.app.models

import com.google.gson.annotations.SerializedName
import com.splintergod.app.R
import com.splintergod.app.assetUrl

data class Focus(
    val name: String,
    @SerializedName("min_rating") val minRating: Int,
    val data: FocusData
)

data class FocusData(
    val description: String,
    val splinter: String? = null,
    val abilities: List<String>? = emptyList()
) {
    fun getAbilityUrls(): List<String> {
        return abilities?.map { ability ->
            "${assetUrl}website/abilities/ability_${
                ability.replace(" ", "-")
            }.png"
        } ?: emptyList()
    }

    fun getSplinterDrawable(): Int? {
        return when (splinter) {
            "Fire" -> R.drawable.element_fire
            "Water" -> R.drawable.element_water
            "Death" -> R.drawable.element_death
            "Dragon" -> R.drawable.element_dragon
            "Earth" -> R.drawable.element_earth
            "Life" -> R.drawable.element_life
            else -> null
        }
    }
}