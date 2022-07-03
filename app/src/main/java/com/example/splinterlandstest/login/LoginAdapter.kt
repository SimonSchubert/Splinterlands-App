package com.example.splinterlandstest.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests

interface OnItemClickListener {
    fun onClickPlayer(player: String)
    fun onDeletePlayer(player: String)
}

class LoginAdapter(
    val dataSet: MutableList<String>,
    val activityViewModel: MainActivityViewModel,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var questsInfo = hashMapOf<String, Requests.QuestResponse>()
    fun updateQuests(quests: HashMap<String, Requests.QuestResponse>) {
        questsInfo = quests
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_PLAYER = 1
        const val VIEW_TYPE_LOGO = 2
        const val VIEW_TYPE_LOGIN = 3
    }

    inner class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val btnDelete: ImageButton

        init {
            textView = view.findViewById(R.id.textView)
            btnDelete = view.findViewById(R.id.btnDelete)
        }

        fun bind(player: String) {
            if (questsInfo.containsKey(player)) {
                val info = questsInfo[player]!!
                textView.text = "$player (${info.getCurrentQuestInfo().chests}C) ${info.getFormattedEndDateShort()}"
            } else {
                textView.text = player
            }

            itemView.setOnClickListener {
                listener.onClickPlayer(player)
            }
            btnDelete.setOnClickListener {
                listener.onDeletePlayer(player)
                dataSet.remove(player)
                notifyDataSetChanged()
            }
        }
    }

    class LogoViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class LoginViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etPlayerName: EditText
        val btnLogin: Button

        init {
            etPlayerName = view.findViewById(R.id.etPlayerName)
            btnLogin = view.findViewById(R.id.btnLogin)
        }

        fun bind() {
            btnLogin.setOnClickListener {
                val player = etPlayerName.text.toString().trim()
                activityViewModel.setPlayer(itemView.context, player)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_PLAYER -> {
                val detailViewHolder = (viewHolder as PlayerViewHolder)
                detailViewHolder.bind(dataSet[position - 1])
            }
            VIEW_TYPE_LOGIN -> {
                val loginViewHolder = (viewHolder as LoginViewHolder)
                loginViewHolder.bind()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOGO -> {
                LogoViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.logo_row_item, viewGroup, false)
                )
            }
            VIEW_TYPE_LOGIN -> {
                LoginViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.login_row_item, viewGroup, false)
                )
            }
            else -> {
                PlayerViewHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.player_row_item, viewGroup, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_LOGO
        } else if (position < dataSet.size + 1) {
            VIEW_TYPE_PLAYER
        } else {
            VIEW_TYPE_LOGIN
        }
    }

    override fun getItemCount() = dataSet.size + 2

}