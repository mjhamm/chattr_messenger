package com.app.message_app_kotlin.bottomnav.ui.friends

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddFriend : AppCompatActivity(), AddFriendsAdapter.OnItemClickListener {

    private lateinit var contactSearch: SearchView
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var list: MutableList<AddFriendObject>
    private lateinit var findPeopletextHeader: TextView
    private lateinit var findPeopleText: TextView
    private lateinit var contentLoading: ContentLoadingProgressBar
    private lateinit var adapter: AddFriendsAdapter
    private var isFriend = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

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
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        list = mutableListOf()
        adapter = AddFriendsAdapter(list, this)
        //fetchUsers()

        val backButton: ImageButton = findViewById(R.id.addContactBack)
        contactSearch = findViewById(R.id.addContactSearch)
        contactRecyclerView = findViewById(R.id.addContactRV)
        findPeopletextHeader = findViewById(R.id.findPeopleHeader)
        findPeopleText = findViewById(R.id.findPeopleText)
        contentLoading = findViewById(R.id.loadingContacts)

        contactRecyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener {
            finish()
        }

        contactSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(char: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(char: String?): Boolean {
                contactRecyclerView.removeAllViews()
                list.clear()
                if (char != null) {
                    if (char.length > 2) {
                        contentLoading.show()
                        hideText(true)
                        fetchUsers(char)
                    } else {
                        contentLoading.hide()
                        hideText(false)
                    }
                }
                return true
            }

        })
    }

    private fun fetchUsers(char: String?) {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val uid = document.get("uid").toString()
                    val image = "M"
                    val firstName = document.get("firstName").toString()
                    val lastName = document.get("lastName").toString()
                    val username = document.get("username").toString()

                    val contact = AddFriendObject(uid, image, firstName, lastName, username)

                    if (firstName.equals(char, ignoreCase = true) || lastName.equals(char, ignoreCase = true) || username.toLowerCase(Locale.ROOT).startsWith(char.toString().toLowerCase(
                            Locale.ROOT))) {
                        if (!list.contains(contact)) {
                            list.add(contact)
                        }
                    }
                }
                if (list.isNotEmpty()) {
                    adapter.notifyDataSetChanged()
                    contentLoading.hide()
                    hideText(true)
                    contactRecyclerView.adapter = adapter
                }
                else {
                    hideText(false)
                    contentLoading.hide()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ADD FRIEND", "Error getting documents: ", exception)
            }
    }

    private fun hideText(hide: Boolean) {
        if (hide) {
            findPeopletextHeader.visibility = View.GONE
            findPeopleText.visibility = View.GONE
        } else {
            findPeopletextHeader.visibility = View.VISIBLE
            findPeopleText.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(position: Int) {
        checkIfRequest(list[position].uid, position)

        Handler(Looper.getMainLooper()).postDelayed({
            contactSearch.setQuery("", false)
            list.clear()
            adapter.notifyDataSetChanged()
            contactRecyclerView.adapter = adapter
        }, 800)
    }

    private fun checkIfRequest(uid: String, position: Int) {

        Log.d("FRIENDSFRAGMENT", uid)
        var isRequest = false
        FirebaseFirestore.getInstance().collection("users")
            .document("rdrwKyPJLLALFJHKsokB")
            .collection("requests")
            .get()
            .addOnSuccessListener { requests ->
                for (request in requests) {
                    Log.d("FRIENDSFRAGMENT", request.id)
                    if (request.id == uid) {
                        isRequest = true
                        val intent = Intent(this, FriendPage::class.java)
                        intent.putExtra("isFriend", 1)
                        intent.putExtra("uid", list[position].uid)
                        intent.putExtra("image", list[position].profileImage)
                        intent.putExtra("username", list[position].username)
                        intent.putExtra("firstName", list[position].firstName)
                        intent.putExtra("lastName", list[position].lastName)
                        startActivity(intent)
                        return@addOnSuccessListener
                    } else {
                        isRequest = false
                    }
                }
                if (!isRequest) {
                    checkIfFriend(uid, position)
                }
            }
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
                        val intent = Intent(this, FriendPage::class.java)
                        intent.putExtra("isFriend", 0)
                        intent.putExtra("uid", list[position].uid)
                        intent.putExtra("image", list[position].profileImage)
                        intent.putExtra("username", list[position].username)
                        intent.putExtra("firstName", list[position].firstName)
                        intent.putExtra("lastName", list[position].lastName)
                        startActivity(intent)
                        return@addOnSuccessListener
                    }
                    else {
                        isFriend = false
                    }
                }
                if (!isFriend) {
                    val intent = Intent(this, FriendPage::class.java)
                    intent.putExtra("isFriend", 2)
                    intent.putExtra("uid", list[position].uid)
                    intent.putExtra("image", list[position].profileImage)
                    intent.putExtra("username", list[position].username)
                    intent.putExtra("firstName", list[position].firstName)
                    intent.putExtra("lastName", list[position].lastName)
                    startActivity(intent)
                }
            }
    }
}