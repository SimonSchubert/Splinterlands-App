package com.example.splinterlandstest.balances

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

class BalancesAdapter :
    RecyclerView.Adapter<BalancesAdapter.ViewHolder>() {

    private var dataSet: List<Requests.BalancesResponse> = emptyList()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    fun updateBalances(dataSet: List<Requests.BalancesResponse>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
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
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = numberFormat.format(dataSet[position].balance.toInt())
        viewHolder.imageView.setImageResource(dataSet[position].getDrawableResource())
    }

    override fun getItemCount() = dataSet.size

}