package com.app.message_app_kotlin.bottomnav.ui.messages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class MessagesAdapter (
    private var messagesList: MutableList<Message>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private var filterList: MutableList<Message> = mutableListOf()

    init {
        filterList.addAll(messagesList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.messages_row,
                        parent,
                        false
                )
        )
    }

    private fun hasUnread(card: CircularRevealCardView, notifications: Int) {
        if (notifications == 0) {
            card.visibility = View.GONE
        }
        card.visibility = View.VISIBLE
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageItem = messagesList[position]

        holder.itemView.apply {
            val image: TextView = findViewById(R.id.imageText)
            val name: TextView = findViewById(R.id.messages_name)
            val message: TextView = findViewById(R.id.message)
            val time: TextView = findViewById(R.id.time)
            val card: CircularRevealCardView = findViewById(R.id.notificationCard)
            val numNotifications: TextView = findViewById(R.id.numNotifications)

            messageItem.apply {
                val fullName = "${this.firstName} ${this.lastName}"
                image.text = this.image
                name.text = fullName
                message.text = this.message
                time.text = this.time
                numNotifications.text = this.notifications.toString()
            }
        }
    }

    override fun getItemCount() = messagesList.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun filter(query: String) {
        Log.d("MESSAGE LIST BEFORE", "${messagesList.size}")
        Log.d("FILTER LIST BEFORE", "${filterList.size}")
        messagesList.clear()
        if (query.isEmpty()) {
            messagesList.addAll(filterList)
        } else {
            for (item in filterList) {
                val fullName = "${item.firstName} ${item.lastName}"
                if (item.firstName.contains(query, ignoreCase = true) or item.lastName.contains(query, ignoreCase = true) or fullName.contains(query, ignoreCase = true)) {
                    messagesList.add(item)
                }
            }
        }
        Log.d("FILTER LIST AFTER", "${filterList.size}")
        Log.d("MESSAGE LIST AFTER", "${messagesList.size}")
        notifyDataSetChanged()
    }
}
