package com.app.message_app_kotlin.bottomnav.ui.chat

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.friends.FriendPage
import com.google.android.material.card.MaterialCardView

class Chat : AppCompatActivity() {

    private var firstName = ""
    private var lastName = ""
    private var username = ""
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        val backButton: ImageButton = findViewById(R.id.chatBack)
        val infoButton: ImageButton = findViewById(R.id.infoButton)
        val chatName: TextView = findViewById(R.id.appBarName)
        val profileImage: MaterialCardView = findViewById(R.id.profileButton)

        if (intent != null) {
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
            username = intent.getStringExtra("username").toString()
            uid = intent.getStringExtra("uid").toString()
            val fullName = "$firstName $lastName"
            chatName.text = fullName
        }

        profileImage.setOnClickListener {
            val intent = Intent(this, FriendPage::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("uid", uid)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        infoButton.setOnClickListener {
            val intent = Intent(this, FriendPage::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("uid", uid)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}