package com.splintergod.app.carddetail

import android.content.Context
import androidx.annotation.DrawableRes
import com.splintergod.app.models.Balances
import com.splintergod.app.models.Card
import com.splintergod.app.models.CardDetail

sealed class CardDetailViewState(val isRefreshing: Boolean) {
    abstract val onRefresh: (context: Context) -> Unit

    data class Loading(override val onRefresh: (context: Context) -> Unit) : CardDetailViewState(true)
    data class Success(override val onRefresh: (context: Context) -> Unit, @DrawableRes val colorIcon: Int, val card: Card, val cardDetail: CardDetail) : CardDetailViewState(false)
    data class Error(override val onRefresh: (context: Context) -> Unit) : CardDetailViewState(false)
}