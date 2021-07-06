package com.app.message_app_kotlin

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.message_app_kotlin.ui.signup.SignUpProfile
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerification : AppCompatActivity() {

    private lateinit var verifiedEmailButton: MaterialButton
    private lateinit var resendVerifyEmail: TextView
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var emailAddress: String
    private lateinit var mAuth: FirebaseAuth

    override fun onResume() {
        super.onResume()

        if (mAuth.currentUser != null) {
            mAuth.currentUser!!.reload()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        val backButton: ImageButton = findViewById(R.id.email_verify_back)
        verifiedEmailButton = findViewById(R.id.verified_button)
        resendVerifyEmail = findViewById(R.id.no_code)

        mAuth = Firebase.auth

        if (intent != null) {
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
            emailAddress = intent.getStringExtra("email").toString()
        }

        backButton.setOnClickListener {
            finish()
        }

        verifiedEmailButton.setOnClickListener {
            if (mAuth.currentUser != null) {
                mAuth.currentUser!!.reload()
            }
            if (mAuth.currentUser?.isEmailVerified == true) {
                val intent = Intent(this, SignUpProfile::class.java)
                intent.putExtra("firstName", firstName)
                intent.putExtra("lastName", lastName)
                intent.putExtra("uid", mAuth.currentUser?.uid)
                intent.putExtra("email", emailAddress)
                intent.putExtra("isPhone", false)
                startActivity(intent)
            } else {
                showDialog(true,
                emailAddress,
                "The above email address has not been verified. Please verify or choose the option below to resend a verification email.",
                "OK",
                "Cancel")
            }
        }
    }

    private fun showDialog(isBasic: Boolean, emailAddress: String, detailText: String, confirmText: String, cancelText: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_code_dialog)
        val titleView = dialog.findViewById<TextView>(R.id.title)
        val detail = dialog.findViewById<TextView>(R.id.detail)
        val confirm = dialog.findViewById<MaterialButton>(R.id.confirm_button)
        val cancel = dialog.findViewById<MaterialButton>(R.id.cancel_button)

        titleView.text = emailAddress
        detail.text = detailText
        confirm.text = confirmText
        cancel.text = cancelText

        confirm.setOnClickListener {
            dialog.dismiss()

            if (!isBasic) {
                mAuth.currentUser?.sendEmailVerification()
            }

        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

        val metrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
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