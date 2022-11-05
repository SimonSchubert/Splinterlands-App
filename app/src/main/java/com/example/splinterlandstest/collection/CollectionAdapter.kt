package com.example.splinterlandstest.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.R
import com.example.splinterlandstest.loadCard
import com.example.splinterlandstest.models.Card
import com.example.splinterlandstest.models.CardDetail
import com.squareup.picasso.Picasso

class CollectionAdapter(private var cardDetails: List<CardDetail>) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    private var dataSet: List<Card> = emptyList()

    fun updateCollection(dataSet: List<Card>) {
        this.dataSet = dataSet.sortedBy { it.card_detail_id }
        notifyDataSetChanged()
    }

    fun updateCardDetails(cardDetails: List<CardDetail>) {
        if (cardDetails.size != this.cardDetails.size) {
            this.cardDetails = cardDetails
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.imageView2)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.image_row_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val card = dataSet[position]
        val cardDetail = cardDetails.firstOrNull { it.id == card.card_detail_id }
        cardDetail?.let {
            Picasso.get().loadCard(viewHolder.imageView, card, cardDetail)
        }
    }

    override fun getItemCount() = dataSet.size

}