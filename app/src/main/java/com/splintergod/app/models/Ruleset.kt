package com.splintergod.app.models

import com.splintergod.app.getRulesetImageUrl

data class Ruleset(val name: String = "", val description: String = "") {
    fun getImageUrl(): String {
        return name.getRulesetImageUrl()
    }
}