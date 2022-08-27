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


class BattlesAdapter(
    val player: String,
    var cardDetails: List<Requests.CardDetail>,
    val gameSettings: Requests.GameSettings,
    val onLickBattle: (battleId: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_DETAILS = 1
        const val VIEW_TYPE_BATTLE = 2
        const val VIEW_TYPE_CURRENT_QUEST = 3
    }


    private var dataSet: List<Requests.Battle> = emptyList()
    private var playerDetails: Requests.PlayerDetailsResponse? = null
    private var rewardsInfo: Requests.RewardsInfo? = null

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

    fun updateRewardsInfo(rewardsInfo: Requests.RewardsInfo?) {
        this.rewardsInfo = rewardsInfo
        notifyDataSetChanged()
    }

    class CurrentQuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvFocusChests: TextView
        private val tvSeasonChests: TextView
        private val tvFocusTimer: TextView
        private val tvSeasonTimer: TextView
        private val ivFocusChest: ImageView
        private val ivSeasonChest: ImageView

        init {
            tvFocusChests = view.findViewById(R.id.tvFocusChests)
            tvSeasonChests = view.findViewById(R.id.tvSeasonChests)
            tvFocusTimer = view.findViewById(R.id.tvFocusTimer)
            tvSeasonTimer = view.findViewById(R.id.tvSeasonTimer)
            ivFocusChest = view.findViewById(R.id.ivFocusChest)
            ivSeasonChest = view.findViewById(R.id.ivSeasonChest)
        }

        lateinit var handlerTask: Runnable

        fun bind(playerQuest: Requests.RewardsInfo?, gameSettings: Requests.GameSettings) {
            if (playerQuest != null) {
                tvFocusChests.text = "Focus chests: ${playerQuest.quest_reward_info.chest_earned}"
                tvSeasonChests.text = "Season chests: ${playerQuest.season_reward_info.chest_earned}"

                Picasso.get()
                    .load(playerQuest.quest_reward_info.getChestUrl())
                    .fit()
                    .into(ivFocusChest)
                Picasso.get()
                    .load(playerQuest.season_reward_info.getChestUrl())
                    .fit()
                    .into(ivSeasonChest)

                handlerTask = Runnable {
                    tvFocusTimer.text = playerQuest.quest_reward_info.getFormattedEndDate()
                    tvSeasonTimer.text = gameSettings.season.getFormattedEndDate()
                    itemView.postDelayed(handlerTask, 1000)
                }
                itemView.removeCallbacks(null)
                itemView.post(handlerTask)
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
            tvPlayerRating.text =
                "W: ${numberFormat.format(playerDetails?.rating)}, M: ${numberFormat.format(playerDetails?.modern_rating)}"
        }
    }

    inner class BattleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView
        private val summoner1: ImageView
        private val monster1_1: ImageView
        private val monster1_2: ImageView
        private val monster1_3: ImageView
        private val monster1_4: ImageView
        private val monster1_5: ImageView
        private val monster1_6: ImageView
        private val summoner2: ImageView
        private val monster2_1: ImageView
        private val monster2_2: ImageView
        private val monster2_3: ImageView
        private val monster2_4: ImageView
        private val monster2_5: ImageView
        private val monster2_6: ImageView
        private val tvPlayer1: TextView
        private val tvPlayer2: TextView
        private val tvPlayer2rating: TextView
        private val tvPlayer1rating: TextView
        private val tvMana: TextView
        private val tvTimeAgo: TextView
        private val tvType: TextView
        private val rulesetImageViews: List<ImageView>

        init {
            imageView = view.findViewById(R.id.imageViewIndicator)
            tvPlayer1 = view.findViewById(R.id.tvPlayer1)
            tvPlayer2 = view.findViewById(R.id.tvPlayer2)
            tvPlayer2rating = view.findViewById(R.id.tvPlayer2Rating)
            tvPlayer1rating = view.findViewById(R.id.tvPlayer1Rating)
            rulesetImageViews = listOf(view.findViewById(R.id.ruleset1), view.findViewById(R.id.ruleset2))
            summoner1 = view.findViewById(R.id.summoner1)
            monster1_1 = view.findViewById(R.id.monster1_1)
            monster1_2 = view.findViewById(R.id.monster1_2)
            monster1_3 = view.findViewById(R.id.monster1_3)
            monster1_4 = view.findViewById(R.id.monster1_4)
            monster1_5 = view.findViewById(R.id.monster1_5)
            monster1_6 = view.findViewById(R.id.monster1_6)
            monster2_1 = view.findViewById(R.id.monster2_1)
            monster2_2 = view.findViewById(R.id.monster2_2)
            monster2_3 = view.findViewById(R.id.monster2_3)
            monster2_4 = view.findViewById(R.id.monster2_4)
            monster2_5 = view.findViewById(R.id.monster2_5)
            monster2_6 = view.findViewById(R.id.monster2_6)

            summoner2 = view.findViewById(R.id.summoner2)
            tvMana = view.findViewById(R.id.tvMana)
            tvTimeAgo = view.findViewById(R.id.tvTimeAgo)
            tvType = view.findViewById(R.id.tvType)
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
                loadCardImage(monster1_1, ownDetail.monsters.getOrNull(0))
                loadCardImage(monster1_2, ownDetail.monsters.getOrNull(1))
                loadCardImage(monster1_3, ownDetail.monsters.getOrNull(2))
                loadCardImage(monster1_4, ownDetail.monsters.getOrNull(3))
                loadCardImage(monster1_5, ownDetail.monsters.getOrNull(4))
                loadCardImage(monster1_6, ownDetail.monsters.getOrNull(5))
            } else {
                summoner1.setImageResource(R.drawable.loose)
            }
            val opponentDetail = battle.getOpponentDetail(battle.getOpponent(player))
            if (opponentDetail != null) {
                loadCardImage(summoner2, opponentDetail.summoner)
                loadCardImage(monster2_1, opponentDetail.monsters.getOrNull(0))
                loadCardImage(monster2_2, opponentDetail.monsters.getOrNull(1))
                loadCardImage(monster2_3, opponentDetail.monsters.getOrNull(2))
                loadCardImage(monster2_4, opponentDetail.monsters.getOrNull(3))
                loadCardImage(monster2_5, opponentDetail.monsters.getOrNull(4))
                loadCardImage(monster2_6, opponentDetail.monsters.getOrNull(5))
            } else {
                summoner2.setImageResource(R.drawable.loose)
            }

            tvTimeAgo.text = battle.getTimeAgo()
            tvType.text = if (battle.match_type == "Ranked") {
                if (battle.format == "modern") {
                    "Modern"
                } else {
                    "Wild"
                }
            } else if (battle.details.is_brawl) {
                "Brawl"
            } else {
                battle.match_type
            }

            itemView.setOnClickListener {
                onLickBattle.invoke(battle.battle_queue_id_1)
            }
        }

        private fun loadCardImage(imageView: ImageView, card: Requests.Card?) {
            val cardDetail = cardDetails.firstOrNull { it.id == card?.card_detail_id }
            if (cardDetail != null && card != null) {
                Picasso.get()
                    .load(card.getPath(cardDetail))
                    .transform(CropSquareTransformation())
                    .transform(CropCircleTransformation())
                    .into(imageView)
                imageView.isVisible = true
            } else {
                imageView.isVisible = false
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
            VIEW_TYPE_CURRENT_QUEST -> (viewHolder as CurrentQuestViewHolder).bind(rewardsInfo, gameSettings)
            else -> {
                var battlesPosition = position
                if (playerDetails != null) {
                    battlesPosition -= 1
                }
                if (rewardsInfo != null) {
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
        } else if (position == 1 && rewardsInfo != null) {
            VIEW_TYPE_CURRENT_QUEST
        } else {
            VIEW_TYPE_BATTLE
        }
    }
}