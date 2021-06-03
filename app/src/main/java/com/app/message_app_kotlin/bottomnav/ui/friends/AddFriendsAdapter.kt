package com.app.message_app_kotlin.bottomnav.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R

class AddFriendsAdapter(
    private var contactsList: MutableList<AddFriendObject>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AddFriendsAdapter.AddContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactViewHolder {
        return AddContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.add_friend_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddContactViewHolder, position: Int) {
        val contactItem = contactsList[position]

        val fullName = "${contactItem.firstName} ${contactItem.lastName}"

        holder.itemView.apply {
            val image: TextView = findViewById(R.id.profileText)
            val name: TextView = findViewById(R.id.contact_name)
            val username: TextView = findViewById(R.id.contact_username)

            contactItem.apply {
                image.text = this.profileImage
                name.text = fullName
                username.text = this.username
            }
        }
    }

    override fun getItemCount() = contactsList.size

    inner class AddContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
