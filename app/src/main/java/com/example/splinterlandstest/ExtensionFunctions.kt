package com.example.splinterlandstest

fun List<Requests.BalancesResponse>.filterBalances(): List<Requests.BalancesResponse> {
    val sps = this.firstOrNull { it.token == "SPS" }
    this.firstOrNull { it.token == "SPSP" }?.let {
        if (sps != null) {
            sps.balance += it.balance
        }
    }
    return this.sortedByDescending { it.balance }
        .filter { it.getDrawableResource() != R.drawable.ic_launcher_background }
}