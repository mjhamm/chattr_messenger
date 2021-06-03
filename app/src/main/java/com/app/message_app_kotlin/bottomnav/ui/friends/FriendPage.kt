package com.app.message_app_kotlin.bottomnav.ui.friends

import android.app.Dialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.app.message_app_kotlin.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.concurrent.TimeUnit

class FriendPage: AppCompatActivity() {

    private lateinit var friendButton: MaterialButton
    private lateinit var friendUid: String

    companion object {
        const val IS_FRIEND = 0
        const val IS_REQUEST = 1
        const val NOT_FRIEND = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_page)

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

        friendButton = findViewById(R.id.friendButton)
        val name = findViewById<TextView>(R.id.contact_name)
        val username = findViewById<TextView>(R.id.contact_username)
        val back = findViewById<ImageButton>(R.id.contact_back)
        if (intent.getStringExtra("uid") != null) {
            friendUid = intent.getStringExtra("uid").toString()
        }
        val isFriend = intent.getIntExtra("isFriend", NOT_FRIEND)
        val profileImage = intent.getStringExtra("image")
        val firstName = intent.getStringExtra("firstName")
        val lastName = intent.getStringExtra("lastName")
        val contactUsername = intent.getStringExtra("username")
        val fullName = "$firstName $lastName"
        name.text = fullName
        username.text = contactUsername

        updateFriendButton(friendButton, isFriend)

        back.setOnClickListener {
            finish()
        }

        friendButton.setOnClickListener {
            when (isFriend) {
                IS_REQUEST -> {
                    showCodeDialog(
                        IS_REQUEST,
                        "Cancel Request?",
                        "Do you want to cancel this friend request?",
                        "Cancel Request",
                        "Dismiss"
                    )
                }
                IS_FRIEND -> {
                    showCodeDialog(
                        IS_REQUEST,
                        "Remove Friend?",
                        "Do you want to remove this friend from your friends list?",
                        "Remove Friend",
                        "Cancel"
                    )
                }
                NOT_FRIEND -> {
                    sendFriendRequest()
                    //showCodeDialog(IS_REQUEST, "", "", "", "")
                }
            }
            sendFriendRequest()
            //addFriend(friendUid)
        }
    }

    private fun sendFriendRequest() {

        val requestedUser = mapOf(
            "uid" to "rdrwKyPJLLALFJHKsokB",
            "request" to true,
            "top" to false
        )

        val friendInfo = mapOf(
            "uid" to friendUid
        )

        FirebaseFirestore.getInstance().collection("users")
            .document(friendUid)
            .collection("friends")
            .document("rdrwKyPJLLALFJHKsokB")
            .set(requestedUser)
            .addOnSuccessListener {
                updateFriendButton(friendButton, IS_REQUEST)
            }

        FirebaseFirestore.getInstance().collection("users")
            .document("rdrwKyPJLLALFJHKsokB")
            .collection("requests")
            .document(friendUid)
            .set(friendInfo)
    }

    private fun checkIfFriend(uid: String?) {
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document("rdrwKyPJLLALFJHKsokB")
                .collection("friends")
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        if (doc.id == uid) {
                            // document with uid exists
                            updateFriendButton(friendButton, IS_FRIEND)
                            FirebaseFirestore.getInstance().collection("users")
                                .document("rdrwKyPJLLALFJHKsokB")
                                .collection("friends")
                                .document(uid)
                                .delete()
                            return@addOnSuccessListener
                        }
                    }
                    // document doesn't exist
                    updateFriendButton(friendButton, IS_REQUEST)
                    val map = mapOf("uid" to uid)
                    FirebaseFirestore.getInstance().collection("users")
                        .document("rdrwKyPJLLALFJHKsokB")
                        .collection("friends")
                        .document(uid)
                        .set(map)
                }
        }
    }

    private fun updateFriendButton(button: MaterialButton, friendStatus: Int) {
        when(friendStatus) {
            IS_FRIEND -> {
                button.setBackgroundColor(getColor(R.color.white))
                button.text = "Unfriend"
                button.iconGravity = MaterialButton.ICON_GRAVITY_START
                button.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_24)
                button.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                button.setIconTintResource(android.R.color.holo_red_dark)
            }
            IS_REQUEST -> {
                button.setBackgroundColor(getColor(R.color.darkerGrey))
                button.text = "Requested"
                button.iconGravity = MaterialButton.ICON_GRAVITY_START
                button.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_add_24)
                button.setTextColor(resources.getColor(android.R.color.white, null))
                button.setIconTintResource(android.R.color.white)
            }
            else -> {
                button.setBackgroundColor(getColor(R.color.blue))
                button.text = "Add Friend"
                button.iconGravity = MaterialButton.ICON_GRAVITY_START
                button.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_add_24)
                button.setTextColor(resources.getColor(R.color.white, null))
                button.setIconTintResource(R.color.white)
            }
        }
    }

    private fun cancelRequest() {

        FirebaseFirestore.getInstance().collection("users")
            .document(friendUid)
            .collection("friends")
            .document("rdrwKyPJLLALFJHKsokB")
            .delete()
            .addOnSuccessListener {
                updateFriendButton(friendButton, NOT_FRIEND)
            }

        FirebaseFirestore.getInstance().collection("users")
            .document("rdrwKyPJLLALFJHKsokB")
            .collection("requests")
            .document(friendUid)
            .delete()

    }

    private fun showCodeDialog(type: Int, title: String, message: String, confirmText: String, cancelText: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_code_dialog)
        val titleView = dialog.findViewById<TextView>(R.id.title)
        val detail = dialog.findViewById<TextView>(R.id.detail)
        val confirm = dialog.findViewById<MaterialButton>(R.id.confirm_button)
        val cancel = dialog.findViewById<MaterialButton>(R.id.cancel_button)

        titleView.text = title
        detail.text = message
        confirm.text = confirmText
        cancel.text = cancelText

        confirm.setOnClickListener {
            dialog.dismiss()

            if (type == IS_REQUEST) {
                cancelRequest()
            }
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

        val metrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = this.display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
        }
        val width = metrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        val dialogWidth = (width * 0.9f)
        val dialogHeight = ConstraintLayout.LayoutParams.WRAP_CONTENT
        layoutParams.width = dialogWidth.toInt()
        layoutParams.height = dialogHeight
        dialog.window?.attributes = layoutParams
    }
}