package com.example.splinterlandstest.battles

import Requests
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.R
import java.text.NumberFormat
import java.util.*

class BattlesAdapter(val player: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_DETAILS = 1
        const val VIEW_TYPE_BATTLE = 2
    }

    private var dataSet: List<Requests.Battle> = emptyList()
    private var playerDetails: Requests.PlayerDetailsResponse? = null

    fun updateBattles(dataSet: List<Requests.Battle>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    fun updatePlayerDetails(dataSet: Requests.PlayerDetailsResponse) {
        this.playerDetails = dataSet
        notifyDataSetChanged()
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
        private val tvPlayer1: TextView
        private val tvPlayer2: TextView
        private val tvPlayer2rating: TextView
        private val tvPlayer1rating: TextView

        init {
            imageView = view.findViewById(R.id.imageViewIndicator)
            tvPlayer1 = view.findViewById(R.id.tvPlayer1)
            tvPlayer2 = view.findViewById(R.id.tvPlayer2)
            tvPlayer2rating = view.findViewById(R.id.tvPlayer2Rating)
            tvPlayer1rating = view.findViewById(R.id.tvPlayer1Rating)
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
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_DETAILS) {
            DetailViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.player_detail_row_item, viewGroup, false)
            )
        } else {
            BattleViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.battle_row_item, viewGroup, false)
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_DETAILS) {
            (viewHolder as DetailViewHolder).bind(playerDetails)
        } else {
            val battlesPosition = if (playerDetails == null) {
                position
            } else {
                position - 1
            }
            (viewHolder as BattleViewHolder).bind(dataSet[battlesPosition])
        }
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && playerDetails != null) VIEW_TYPE_DETAILS else VIEW_TYPE_BATTLE
    }
}