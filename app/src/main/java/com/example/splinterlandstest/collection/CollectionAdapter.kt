package com.example.splinterlandstest.collection

import Requests
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.R
import com.squareup.picasso.Picasso

class CollectionAdapter :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    private var dataSet: List<Requests.Card> = emptyList()
    private var cardDetails: List<Requests.CardDetail> = emptyList()

    fun updateCollection(dataSet: List<Requests.Card>) {
        this.dataSet = dataSet.sortedBy { it.card_detail_id }
        notifyDataSetChanged()
    }

    fun updateCardDetails(cardDetails: List<Requests.CardDetail>) {
        this.cardDetails = cardDetails
        notifyDataSetChanged()
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
            Picasso.get()
                .load("https://d36mxiodymuqjm.cloudfront.net/${card.getPath()}/${cardDetail.name}.${card.getFileEnding()}")
                .placeholder(card.getPlaceholderDrawable())
                .fit()
                .into(viewHolder.imageView)
        }
    }

    override fun getItemCount() = dataSet.size

}