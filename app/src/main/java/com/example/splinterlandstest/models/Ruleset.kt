package com.example.splinterlandstest.models

import com.example.splinterlandstest.getRulesetImageUrl

data class Ruleset(val name: String = "", val description: String = "") {
    fun getImageUrl(): String {
        return name.getRulesetImageUrl()
    }
}