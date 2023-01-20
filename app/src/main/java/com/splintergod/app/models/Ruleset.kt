package com.splintergod.app.models

import com.splintergod.app.toRulesetImageUrl

data class Ruleset(val active: Boolean = true, val name: String = "", val description: String = "") {
    fun getImageUrl(): String {
        return name.toRulesetImageUrl()
    }
}