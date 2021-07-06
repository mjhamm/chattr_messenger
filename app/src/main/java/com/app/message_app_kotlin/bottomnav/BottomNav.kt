package com.app.message_app_kotlin.bottomnav

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.app.message_app_kotlin.SignIn
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.friends.AddFriend
import com.app.message_app_kotlin.bottomnav.ui.friends.FriendsFragment
import com.app.message_app_kotlin.bottomnav.ui.messages.MessageFragment
import com.app.message_app_kotlin.bottomnav.ui.messages.NewMessage
import com.app.message_app_kotlin.profile.UpdateProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class BottomNav : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var messageFragment: MessageFragment
    private lateinit var friendsFragment: FriendsFragment
    private lateinit var active: Fragment
    private lateinit var mAuth: FirebaseAuth
    private lateinit var nameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var myUID: String
    private var myName = ""
    private var myUsername = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

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

        mAuth = Firebase.auth
        val fm = supportFragmentManager

        if (mAuth.currentUser != null) {
            myUID = mAuth.currentUser!!.uid
        }

        drawer = findViewById(R.id.drawer)
        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        val drawerNav: NavigationView = findViewById(R.id.drawer_nav)
        val profileButton: MaterialCardView = findViewById(R.id.profileButton)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val appBarTitle: TextView = findViewById(R.id.appBar_title)

        val navHeader: View = drawerNav.getHeaderView(0)
        nameTextView = navHeader.findViewById(R.id.textName)
        usernameTextView = navHeader.findViewById(R.id.textUsername)

        retrieveUserInfo(myUID)

        FirebaseFirestore.getInstance().collection("users")
            .document(myUID)
            .addSnapshotListener { value, error ->
                val firstName = value?.getString("firstName")
                val lastName = value?.getString("lastName")
                val username = value?.getString("username")

                val name = "$firstName $lastName"

                setHeader(name, username.toString())
            }

        messageFragment = MessageFragment()
        friendsFragment = FriendsFragment()
        // setup the message fragment as the active fragment
        active = messageFragment

        // add each fragment to the activity
        // this will make showing and hiding inside of the fragment container easier when the user
        // clicks on the corresponding item in the bottom nav bar
        fm.beginTransaction().add(R.id.nav_host_fragment, messageFragment, "message").commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, friendsFragment, "contact").hide(friendsFragment).commit()//.hide(contactsFragment).commit()

        appBarTitle.text = getString(R.string.title_messages)

        var curDrawable = R.drawable.new_message_48

        navView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_messages -> {
                    fm.beginTransaction().hide(active).show(messageFragment).commit()
                    appBarTitle.text = "Messages"
                    active = messageFragment
                    if (curDrawable != R.drawable.new_message_48) {
                        animation(addButton, R.drawable.new_message_48)
                        curDrawable = R.drawable.new_message_48
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_contacts -> {
                    fm.beginTransaction().hide(active).show(friendsFragment).commit()
                    appBarTitle.text = "Friends"
                    active = friendsFragment
                    if (curDrawable != R.drawable.ic_baseline_person_add_alt_24) {
                        animation(addButton, R.drawable.ic_baseline_person_add_alt_24)
                        curDrawable = R.drawable.ic_baseline_person_add_alt_24
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

        profileButton.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.close()
            } else {
                drawer.openDrawer(GravityCompat.END)
            }
        }

        addButton.setOnClickListener {
            if(curDrawable == R.drawable.new_message_48) {
                val intent = Intent(this, NewMessage::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, AddFriend::class.java)
                startActivity(intent)
            }
        }

        drawerNav.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_notifications -> {
                    drawer.closeDrawer(GravityCompat.END)
                    true
                }
                R.id.update_profile -> {
                    val intent = Intent(this, UpdateProfile::class.java)
                    drawer.closeDrawer(GravityCompat.END)
                    startActivity(intent)
                    true
                }
                R.id.signOut -> {
                    mAuth.signOut()
                    finish()
                    val intent = Intent(this, SignIn::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    drawer.closeDrawer(GravityCompat.END)
                    startActivity(intent)
                    true
                }
                else -> {
                    drawer.closeDrawer(GravityCompat.END)
                    true
                }
            }
        }
    }

    private fun animation(button: FloatingActionButton, drawable: Int) {
            button.animate()
                    .setDuration(100)
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .withEndAction(Runnable {
                        kotlin.run {
                            button.setImageResource(drawable)

                            button.animate()
                                    .setDuration(100)
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .start()
                        }
                    })
                    .start()
    }

    private fun retrieveUserInfo(uid: String) {

        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { user ->
                val firstName = user.getString("firstName")
                val lastName = user.getString("lastName")
                val username = user.getString("username") as String

                myName = "$firstName $lastName"
                myUsername = username

                setHeader(myName, myUsername)
            }

    }

    private fun setHeader(name: String, username: String) {
        nameTextView.text = name
        usernameTextView.text = username
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
            finish()
        }
    }
}