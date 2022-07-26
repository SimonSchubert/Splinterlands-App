package com.example.splinterlandstest.rewards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.loadCard
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class RewardsAdapter :
    RecyclerView.Adapter<RewardsAdapter.ViewHolder>() {

    private var dataSet: List<Requests.Reward> = emptyList()
    private var cardDetails: List<Requests.CardDetail> = emptyList()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    fun updateRewards(dataSet: List<Requests.Reward>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    fun updateCardDetails(dataSet: List<Requests.CardDetail>) {
        this.cardDetails = dataSet
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView

        init {
            textView = view.findViewById(R.id.textView)
            imageView = view.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.reward_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (val reward = dataSet[position]) {
            is Requests.DecReward -> {
                viewHolder.textView.text = "${numberFormat.format(reward.quantity)} DEC"
                viewHolder.imageView.setImageResource(R.drawable.dec)
            }
            is Requests.CreditsReward -> {
                viewHolder.textView.text = "${numberFormat.format(reward.quantity)} CREDITS"
                viewHolder.imageView.setImageResource(R.drawable.credits)
            }
            is Requests.MeritsReward -> {
                viewHolder.textView.text = "${numberFormat.format(reward.quantity)} Merits"
                viewHolder.imageView.setImageResource(R.drawable.mertis)
            }
            is Requests.PackReward -> {
                viewHolder.textView.text = "PACK"
                viewHolder.imageView.setImageResource(R.drawable.chaos)
            }
            is Requests.CardReward -> {
                val card = Requests.Card(reward.cardId.toString(), 3, reward.isGold)

                val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }

                if (cardDetail != null) {
                    viewHolder.textView.text = cardDetail.name
                    Picasso.get().loadCard(viewHolder.imageView, card, cardDetail)
                }
            }
            is Requests.GoldPotionReward -> {
                viewHolder.textView.text = "${reward.quantity} POTION"
                viewHolder.imageView.setImageResource(R.drawable.gold)
            }
            is Requests.LegendaryPotionReward -> {
                viewHolder.textView.text = "${reward.quantity} POTION"
                viewHolder.imageView.setImageResource(R.drawable.legendary)
            }
        }
    }

    override fun getItemCount() = dataSet.size

}