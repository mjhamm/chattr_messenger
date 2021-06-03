package com.app.message_app_kotlin.bottomnav.ui.friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FriendsFragment : Fragment(), FriendsAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendsAdapter
    private lateinit var list: MutableList<Friend>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var friendsList: MutableList<Friend>
    private lateinit var friendsLoading: ContentLoadingProgressBar
    private lateinit var myUID: String
    private lateinit var friendsTextHeader: TextView
    private lateinit var friendsTextDetail: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_friends, container, false)

        mAuth = FirebaseAuth.getInstance()
        recyclerView = root.findViewById(R.id.contact_recyclerview)
        friendsLoading = root.findViewById(R.id.friendsLoading)
        friendsTextHeader = root.findViewById(R.id.friends_text)
        friendsTextDetail = root.findViewById(R.id.friends_detail)

        list = mutableListOf()
        friendsList = mutableListOf()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // user already signed in
            myUID = currentUser.uid
        }

        fetchFriends()

        return root
    }

    override fun onItemClick(position: Int) {
        checkIfFriend(friendsList[position].uid, position)
    }

    private fun fetchFriends() {
        friendsLoading.show()

        val requestUidList = mutableListOf<String>()
        val friendTempList = mutableListOf<Pair<String, Boolean?>>()

        FirebaseFirestore.getInstance().collection("users")
            .document(myUID)
            .collection("friends")
            .get()
            .addOnSuccessListener { friends ->
                for (friend in friends) {
                    val isRequest = friend.getBoolean("request")
                    val isTop = friend.getBoolean("top")
                    val uid = friend.getString("uid")
                    if (isRequest != null && uid != null) {
                        if (isRequest) {
                            // is friend request
                            requestUidList.add(uid)
                        }
                        else {
                            // is friend
                            friendTempList.add(Pair(uid, isTop))
                        }
                    }
                }
                // so requests always stay on top of list
                if (requestUidList.isNotEmpty()) {
                    getRequestInfo(requestUidList, friendTempList)
                } else {
                    // if there is no requests, get friends
                    getFriendInfo(friendTempList)
                }
            }
    }

    private fun hideNoFriendsText(hide: Boolean) {
        if (hide) {
            friendsTextHeader.visibility = View.GONE
            friendsTextDetail.visibility = View.GONE
        } else {
            friendsTextHeader.visibility = View.VISIBLE
            friendsTextDetail.visibility = View.VISIBLE
        }
    }

    private fun getRequestInfo(list: MutableList<String>, friendTempList: MutableList<Pair<String, Boolean?>>) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        var remainingQueries = list.size

        for (uid in list) {
            usersRef.document(uid).get()
                .addOnSuccessListener { user ->
                    val image = "M"
                    val firstName = user.get("firstName").toString()
                    val lastName = user.get("lastName").toString()
                    val username = user.get("username").toString()

                    val request = Friend(0, uid, image, firstName, lastName, username)

                    friendsList.add(request)

                    if (--remainingQueries == 0) {
                        if (friendTempList.isNotEmpty()) {
                            getFriendInfo(friendTempList)
                        }
                    }
                }
        }
    }

    private fun getFriendInfo(list: MutableList<Pair<String, Boolean?>>) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        var remainingQueries = list.size

        for (uid in list) {
            usersRef.document(uid.first).get()
                .addOnSuccessListener { user ->

                    val image = "M"
                    val firstName = user.getString("firstName").toString()
                    val lastName = user.getString("lastName").toString()
                    val username = user.get("username").toString()

                    val fullName = "$firstName $lastName"

                    val request = Friend(1, uid.first, image, firstName, lastName, username, false,
                        uid.second!!
                    )
                    friendsList.add(request)

                    if (--remainingQueries == 0) {
                        if (friendsList.isEmpty()) {
                            hideNoFriendsText(true)
                        } else {
                            hideNoFriendsText(false)
                            setAdapter(friendsList, fullName)
                        }
                    }
                }
        }
    }

    private fun setAdapter(list: MutableList<Friend>, fullName: String) {
        list.sortBy { fullName.toLowerCase(Locale.ROOT) }
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

        friendsLoading.hide()

        adapter = FriendsAdapter(list, this)
        recyclerView.layoutManager = LinearLayoutManager(parentFragment?.context)
        recyclerView.adapter = adapter
    }

    private fun checkIfFriend(uid: String, position: Int) {
        var isFriend = false
        FirebaseFirestore.getInstance().collection("users")
            .document("rdrwKyPJLLALFJHKsokB")
            .collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc.id == uid) {
                        val intent = Intent(context, FriendPage::class.java)
                        intent.putExtra("isFriend", 0)
                        intent.putExtra("uid", friendsList[position].uid)
                        intent.putExtra("image", friendsList[position].image)
                        intent.putExtra("username", friendsList[position].username)
                        intent.putExtra("firstName", friendsList[position].firstName)
                        intent.putExtra("lastName", friendsList[position].lastName)
                        startActivity(intent)
                        return@addOnSuccessListener
                    }
                    else {
                        isFriend = false
                    }
                }
                if (!isFriend) {
                    val intent = Intent(context, FriendPage::class.java)
                    intent.putExtra("isFriend", 2)
                    intent.putExtra("uid", friendsList[position].uid)
                    intent.putExtra("image", friendsList[position].image)
                    intent.putExtra("username", friendsList[position].username)
                    intent.putExtra("firstName", friendsList[position].firstName)
                    intent.putExtra("lastName", friendsList[position].lastName)
                    startActivity(intent)
                }
            }
    }
}