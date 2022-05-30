package com.example.splinterlandstest

fun List<Requests.BalancesResponse>.filterBalances(): List<Requests.BalancesResponse> {
    val sps = this.firstOrNull { it.token == "SPS" }
    this.firstOrNull { it.token == "SPSP" }?.let {
        if (sps != null) {
            sps.balance += it.balance
        }
    }
    return this.sortedByDescending { it.balance }
        .filter { it.token != "ECR" && it.token != "SPSP" && it.token != "QUEST" && it.token != "PWRSTONE" && it.token != "BLDSTONE" && it.token != "RAFFLE" && it.token != "TOTEMC" && it.token != "TOTEMR" && it.token != "WAKA_COMMON" && it.token != "MYSTERY" && it.token != "TOTEME" && it.token != "TRACT"}
}