package com.app.message_app_kotlin.ui.signup

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.message_app_kotlin.EmailVerification
import com.app.message_app_kotlin.PhoneVerification
import com.app.message_app_kotlin.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpEmail : AppCompatActivity() {

    private lateinit var nextButton: MaterialButton
    private lateinit var emailEditText: EditText
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tempPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_email)

        val backButton: ImageButton = findViewById(R.id.signUpEmailBack)
        nextButton = findViewById(R.id.nextButton)
        emailEditText = findViewById(R.id.emailEditText)

        nextButton.isEnabled = false
        nextButton.isClickable = false

        tempPassword = "98714FHUsudhfOWIUDb$09854FPHUHAUT"

        mAuth = Firebase.auth

        if (intent != null) {
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
        }

        backButton.setOnClickListener {
            finish()
        }

        mAuth.addAuthStateListener {
            Log.d("TAG", "auth state changed")
            mAuth.currentUser?.sendEmailVerification()
        }

        nextButton.setOnClickListener {
            showDialog(
                false,
                emailEditText.text.toString(),
                "An email verification will be sent to the above email address. Is this email correct?",
                "Yes",
                "Edit Email")
        }

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disableButton(p0.toString(), nextButton)
            }

            override fun afterTextChanged(p0: Editable?) {
                disableButton(p0.toString(), nextButton)
            }

        })

    }

    private fun disableButton(email: String, button: MaterialButton) {
        if (!email.contains("@") || email.isEmpty()) {
            button.isEnabled = false
            button.isClickable = false
        } else {
            button.isEnabled = true
            button.isClickable = true
        }
    }

    private fun sendEmailVerification(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        val user = mAuth.currentUser
                        if (user != null) {
                            Log.d("TAG", "user not null")
                            //mAuth.currentUser?.sendEmailVerification()
                            val intent = Intent(this, EmailVerification::class.java)
                            intent.putExtra("firstName", firstName)
                            intent.putExtra("lastName", lastName)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        }
                    }
                }

//                if (it.isSuccessful) {
//
//                    }
//                } else {
//                    showDialog(
//                        true,
//                        emailEditText.text.toString(),
//                        "This email address is already in use by another account. Please Sign In, or Sign Up with a different email address.",
//                        "OK",
//                        "Cancel")
//                }
            }
            .addOnFailureListener {
                showDialog(
                    true,
                    emailEditText.text.toString(),
                    it.message.toString(),
                    "OK",
                    "Cancel"
                )
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
                sendEmailVerification(emailEditText.text.toString(), tempPassword)
            }

        }
        cancel.setOnClickListener {
            dialog.dismiss()
            this.emailEditText.requestFocus()
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