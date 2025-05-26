package com.splintergod.app.carddetail

import android.content.Context
import androidx.annotation.DrawableRes
import com.splintergod.app.models.Ability
import com.splintergod.app.models.Card
import com.splintergod.app.models.CardDetail

sealed class CardDetailViewState {

    data class Loading() :
        CardDetailViewState()

    data class Success(
        @DrawableRes val colorIcon: Int,
        val card: Card,
        val cardDetail: CardDetail,
        val abilities: List<Ability>
    ) : CardDetailViewState()

    data class Error(val message: String? = null) :
        CardDetailViewState()
}