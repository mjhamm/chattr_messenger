package com.app.message_app_kotlin.bottomnav.ui.friends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.messages.Message
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FriendsAdapter(
    private var friendsList: MutableList<Friend>,
    private val listener: OnItemClickListener,
    private val myUID: String

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filterList: MutableList<Friend> = mutableListOf()

    init {
        filterList.addAll(friendsList)
    }

    companion object {
        const val FRIEND_REQUEST_ITEM = 0
        const val FRIEND_ITEM = 1
        const val HEADER_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            FRIEND_REQUEST_ITEM -> {
                return FriendRequestViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.friend_request_row, parent, false)
                )
            }
            FRIEND_ITEM -> {
                return FriendViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.friends_row, parent, false)
                )
            }
            else -> {
                return ViewHeader(
                    LayoutInflater.from(parent.context).inflate(R.layout.list_header_row, parent, false)
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (friendsList[position].viewType) {
            HEADER_ITEM -> {
                val headerItem = friendsList[position]

                holder.itemView.apply {
                    val headerText: TextView = findViewById(R.id.headerText)

                    headerItem.apply {
                        headerText.text = this.firstName
                    }
                }
            }
            FRIEND_ITEM -> {
                val friendItem = friendsList[position]

                holder.itemView.apply {
                    val image: TextView = findViewById(R.id.profileText)
                    val name: TextView = findViewById(R.id.contact_name)
                    val username: TextView = findViewById(R.id.contact_username)
                    val online: MaterialCardView = findViewById(R.id.onlineIndicator)

                    friendItem.apply {

                        val fullName = "${this.firstName} ${this.lastName}"
                        image.text = this.image
                        name.text = fullName
                        username.text = this.username

                        if (friendItem.isOnline) {
                            online.visibility = View.VISIBLE
                        } else {
                            online.visibility = View.GONE
                        }
                    }
                }
            }
            else -> {
                val friendRequestItem = friendsList[position]

                holder.itemView.apply {
                    val image: TextView = findViewById(R.id.friendRequestProfileText)
                    val name: TextView = findViewById(R.id.friendRequest_name)
                    val username: TextView = findViewById(R.id.friendRequest_username)

                    friendRequestItem.apply {
                        val fullName = "${this.firstName} ${this.lastName}"
                        image.text = this.image
                        name.text = fullName
                        username.text = this.username
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(friendsList[position].viewType) {
            FRIEND_REQUEST_ITEM -> {
                FRIEND_REQUEST_ITEM
            }
            FRIEND_ITEM -> {
                FRIEND_ITEM
            }
            else -> {
                HEADER_ITEM
            }
        }
    }

    override fun getItemCount() = friendsList.size

    fun sortAdapter(list: MutableList<Friend>) {
        list.sortBy { it.firstName.lowercase(Locale.ROOT) }
        list.sortByDescending { it.top }
        list.sortBy { it.viewType }

        // adds headers to top and friends
        // TODO - Make this more efficient somehow (maybe function in adapter while sort happens?)
        var count = 0
        var friendsHeaderAdded = false
        var topHeaderAdded = false

        for (index in list.indices) {
            if (list[count].viewType > 0) {
                if (list[count].top) {
                    if (!topHeaderAdded) {
                        list.add(count, Friend(2, "", "", "Top Friends", "", ""))
                        ++count
                        topHeaderAdded = true
                    }
                } else {
                    if (!friendsHeaderAdded) {
                        list.add(count, Friend(2, "", "", "Friends", "", ""))
                        friendsHeaderAdded = true
                    }
                }
            }
            ++count
        }

        notifyDataSetChanged()
    }

    fun filter(query: String) {
        friendsList.clear()
        if (query.isEmpty()) {
            friendsList.addAll(filterList)
        } else {
            for (item in filterList) {
                val fullName = "${item.firstName} ${item.lastName}"
                if (item.firstName.contains(query, ignoreCase = true)
                    or item.lastName.contains(query, ignoreCase = true)
                    or fullName.contains(query, ignoreCase = true)
                    and (item.viewType != 2)) {
                    friendsList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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

    inner class ViewHeader(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Friend Request
    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val acceptButton: ImageButton = itemView.findViewById(R.id.acceptRequest)
        private val denyRequest: ImageButton = itemView.findViewById(R.id.denyRequest)

        init {
            // accept button click listener
            acceptButton.setOnClickListener {
                val position = adapterPosition
                val uid = friendsList[position].uid
                if (position != RecyclerView.NO_POSITION) {
                    val map = mapOf("uid" to uid, "request" to false, "top" to false)
                    // adds friend to firebase
                    FirebaseFirestore.getInstance().collection("users")
                        .document(myUID)
                        .collection("friends")
                        .document(uid)
                        .set(map)

                    val infoToFriend = mapOf(
                        "uid" to myUID,
                        "request" to false,
                        "top" to false
                    )

                    FirebaseFirestore.getInstance().collection("users")
                        .document(friendsList[position].uid)
                        .collection("friends")
                        .document(myUID)
                        .set(infoToFriend)

                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { user ->
                            val image = "M"
                            val firstName = user.get("firstName").toString()
                            val lastName = user.get("lastName").toString()
                            val username = user.get("username").toString()

                            val friend = Friend(1, uid, image, firstName, lastName, username)

                            // remove the request item
                            friendsList.removeAt(position)
                            // add the new friend item
                            friendsList.add(friend)
                            // notify the adapter that the item changed
                            notifyDataSetChanged()
                        }

                    FirebaseFirestore.getInstance().collection("users")
                        .document(friendsList[position].uid)
                        .collection("requests")
                        .document(myUID)
                        .delete()
                }
            }

            // deny button click listener
            denyRequest.setOnClickListener {
                val position = adapterPosition
                val uid = friendsList[position].uid
                if (position != RecyclerView.NO_POSITION) {
                    // removes friend request from firebase
                    FirebaseFirestore.getInstance().collection("users")
                        .document(myUID)
                        .collection("friends")
                        .document(uid)
                        .delete()

                    friendsList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
