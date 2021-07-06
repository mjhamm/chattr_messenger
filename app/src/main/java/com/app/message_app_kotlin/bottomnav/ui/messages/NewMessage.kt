package com.app.message_app_kotlin.bottomnav.ui.messages

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.friends.Friend
import com.app.message_app_kotlin.bottomnav.ui.friends.FriendsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewMessage : AppCompatActivity(), FriendsAdapter.OnItemClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var myUID: String
    private lateinit var list: MutableList<Friend>
    private lateinit var adapter: FriendsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var uidList: MutableList<String>
    private lateinit var contentLoading: ContentLoadingProgressBar
    private lateinit var noFriendsHeader: TextView
    private lateinit var noFriendsDetail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        val mode = applicationContext?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        when(mode) {
            Configuration.UI_MODE_NIGHT_YES -> window.statusBarColor = getColor(R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> window.statusBarColor = getColor(R.color.white)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> window.statusBarColor = getColor(R.color.white)
            else -> window.statusBarColor = getColor(R.color.white)
        }

        if (mode == Configuration.UI_MODE_NIGHT_NO || mode == Configuration.UI_MODE_NIGHT_UNDEFINED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR// or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            myUID = mAuth.currentUser!!.uid
        }

        list = mutableListOf()
        uidList = mutableListOf()
        adapter = FriendsAdapter(list, this, myUID)

        contentLoading = findViewById(R.id.friendsLoading)
        recyclerView = findViewById(R.id.newMessageRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        noFriendsHeader = findViewById(R.id.no_friends_text)
        noFriendsDetail = findViewById(R.id.no_friends_detail)
        val backButton: ImageButton = findViewById(R.id.newBack)

        backButton.setOnClickListener {
            finish()
        }

        fetchFriends()
    }

    private fun hideText(hide: Boolean) {
        if (hide) {
            noFriendsHeader.visibility = View.GONE
            noFriendsDetail.visibility = View.GONE
        } else {
            noFriendsHeader.visibility = View.VISIBLE
            noFriendsDetail.visibility = View.VISIBLE
        }
    }

    private fun fetchFriends() {
        // fetch friends from Firebase
        contentLoading.show()
        FirebaseFirestore.getInstance().collection("users")
            .document(myUID)
            .collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val isRequest = document.getBoolean("request") as Boolean
                    if (!isRequest) {
                        uidList.add(document.id)
                    }
                }
                if (uidList.isNotEmpty()) {
                    retrieveFriends(uidList)
                    hideText(true)
                } else {
                    hideText(false)
                    contentLoading.hide()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ADD FRIEND", "Error getting documents: ", exception)
            }
    }

    private fun retrieveFriends(uids: MutableList<String>) {

        Log.d("NEWMESSAGE", uids.toString())
        var remainingQueries = uids.size

        val usersRef = FirebaseFirestore.getInstance().collection("users")

        for (friend in uids) {
            usersRef.document(friend).get()
                .addOnSuccessListener { document ->
                    val uid = document.get("uid").toString()
                    val image = "M"
                    val firstName = document.get("firstName").toString()
                    val lastName = document.get("lastName").toString()
                    val username = document.get("username").toString()

                    val contact = Friend(1, uid, image, firstName, lastName, username)

                    list.add(contact)

                    if (--remainingQueries == 0) setAdapter(adapter, recyclerView)
                }
        }
    }

    private fun setAdapter(adapter: FriendsAdapter, recyclerView: RecyclerView) {
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        hideText(true)
        contentLoading.hide()
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(baseContext, list[position].username, Toast.LENGTH_SHORT).show()
    }
}