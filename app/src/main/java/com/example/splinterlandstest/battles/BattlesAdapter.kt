package com.example.splinterlandstest.battles

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.text.NumberFormat
import java.util.*


class BattlesAdapter(val player: String, var cardDetails: List<Requests.CardDetail>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_DETAILS = 1
        const val VIEW_TYPE_BATTLE = 2
        const val VIEW_TYPE_CURRENT_QUEST = 3
    }


    private var dataSet: List<Requests.Battle> = emptyList()
    private var playerDetails: Requests.PlayerDetailsResponse? = null
    private var playerQuest: Requests.QuestResponse? = null

    fun updateBattles(dataSet: List<Requests.Battle>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    fun updateCardDetails(cardDetails: List<Requests.CardDetail>) {
        if (cardDetails.size != this.cardDetails.size) {
            this.cardDetails = cardDetails
            notifyDataSetChanged()
        }
    }

    fun updatePlayerDetails(playerDetails: Requests.PlayerDetailsResponse) {
        this.playerDetails = playerDetails
        notifyDataSetChanged()
    }

    fun updatePlayerQuest(playerQuest: Requests.QuestResponse?) {
        this.playerQuest = playerQuest
        notifyDataSetChanged()
    }

    class CurrentQuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvNextChest: TextView
        private val tvChests: TextView
        private val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        private val ivChest: ImageView

        init {
            tvNextChest = view.findViewById(R.id.tvNextChest)
            tvChests = view.findViewById(R.id.tvChests)
            ivChest = view.findViewById(R.id.ivChest)
        }

        fun bind(playerQuest: Requests.QuestResponse?) {
            val questInfo = playerQuest?.getCurrentQuestInfo()
            if (questInfo != null) {
                tvNextChest.text =
                    "FP to next chest: ${numberFormat.format(questInfo.nextChestRshares - questInfo.requiredRshares)}/${
                        numberFormat.format(questInfo.nextChestRshares)
                    }"
                tvChests.text = "Focus chests: ${questInfo.chests}"
                Picasso.get()
                    .load(questInfo.getChestUrl())
                    .fit()
                    .into(ivChest)
            }
        }
    }

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvPlayerName: TextView
        private val tvPlayerRating: TextView
        private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

        init {
            tvPlayerName = view.findViewById(R.id.tvPlayerName)
            tvPlayerRating = view.findViewById(R.id.tvPlayerRating)
        }

        fun bind(playerDetails: Requests.PlayerDetailsResponse?) {
            tvPlayerName.text = playerDetails?.name
            tvPlayerRating.text = numberFormat.format(playerDetails?.rating)
        }
    }

    inner class BattleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView
        private val summoner1: ImageView
        private val summoner2: ImageView
        private val tvPlayer1: TextView
        private val tvPlayer2: TextView
        private val tvPlayer2rating: TextView
        private val tvPlayer1rating: TextView
        private val tvMana: TextView
        private val rulesetImageViews: List<ImageView>

        init {
            imageView = view.findViewById(R.id.imageViewIndicator)
            tvPlayer1 = view.findViewById(R.id.tvPlayer1)
            tvPlayer2 = view.findViewById(R.id.tvPlayer2)
            tvPlayer2rating = view.findViewById(R.id.tvPlayer2Rating)
            tvPlayer1rating = view.findViewById(R.id.tvPlayer1Rating)
            rulesetImageViews = listOf(view.findViewById(R.id.ruleset1), view.findViewById(R.id.ruleset2))
            summoner1 = view.findViewById(R.id.summoner1)
            summoner2 = view.findViewById(R.id.summoner2)
            tvMana = view.findViewById(R.id.tvMana)
        }

        fun bind(battle: Requests.Battle) {
            if (battle.isWin(player)) {
                imageView.setImageResource(R.drawable.win)
            } else {
                imageView.setImageResource(R.drawable.loose)
            }

            tvPlayer1.text = player
            tvPlayer2.text = battle.getOpponent(player)
            tvPlayer1rating.text = battle.getOwnRating(player)
            tvPlayer2rating.text = battle.getOpponentRating(player)
            tvMana.text = battle.mana_cap.toString()

            val rulesetImagePathes = battle.getRulesetImagePaths()
            rulesetImagePathes.forEachIndexed { index, s ->
                Picasso.get()
                    .load(s)
                    .fit()
                    .into(rulesetImageViews[index])
            }
            rulesetImageViews[1].isVisible = rulesetImagePathes.size > 1

            val ownDetail = battle.getOwnDetail(player)
            if (ownDetail != null) {
                loadCardImage(summoner1, ownDetail.summoner)
            } else {
                summoner1.setImageResource(R.drawable.loose)
            }
            val opponentDetail = battle.getOpponentDetail(battle.getOpponent(player))
            if (opponentDetail != null) {
                loadCardImage(summoner2, opponentDetail.summoner)
            } else {
                summoner2.setImageResource(R.drawable.loose)
            }
        }

        private fun loadCardImage(imageView: ImageView, card: Requests.Card) {
            val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }
            if (cardDetail != null) {
                Picasso.get()
                    .load(
                        "https://d36mxiodymuqjm.cloudfront.net/${card.getPath()}/${cardDetail.name}.${
                            card.getFileEnding(
                                cardDetail
                            )
                        }"
                    )
                    .transform(CropSquareTransformation())
                    .transform(CropCircleTransformation())
                    .into(imageView)
            }
        }
    }

    class CropSquareTransformation : Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val size = (source.width * 0.6f).toInt()
            val x = (source.width - size) / 2
            val y = source.height * 0.1f
            val result = Bitmap.createBitmap(source, x, y.toInt(), size, size)

            source.recycle()

            return result
        }

        override fun key(): String {
            return "square()"
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DETAILS -> {
                DetailViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.player_detail_row_item, viewGroup, false)
                )
            }
            VIEW_TYPE_CURRENT_QUEST -> {
                CurrentQuestViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.player_quest_row_item, viewGroup, false)
                )
            }
            else -> {
                BattleViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.battle_row_item, viewGroup, false)
                )
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_DETAILS -> (viewHolder as DetailViewHolder).bind(playerDetails)
            VIEW_TYPE_CURRENT_QUEST -> (viewHolder as CurrentQuestViewHolder).bind(playerQuest)
            else -> {
                var battlesPosition = position
                if (playerDetails != null) {
                    battlesPosition -= 1
                }
                if (playerQuest != null) {
                    battlesPosition -= 1
                }
                (viewHolder as BattleViewHolder).bind(dataSet[battlesPosition])
            }
        }
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && playerDetails != null) {
            VIEW_TYPE_DETAILS
        } else if (position == 1 && playerQuest != null) {
            VIEW_TYPE_CURRENT_QUEST
        } else {
            VIEW_TYPE_BATTLE
        }
    }
}