package com.example.splinterlandstest.models

data class Focus(val name: String, val min_rating: Int, val data: FocusData) {

    fun getImageUrl(): String {
        return ""
    }
}

data class FocusData(val description: String)