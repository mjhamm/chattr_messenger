package com.app.message_app_kotlin.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.BottomNav
import com.app.message_app_kotlin.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var whatToKnowHeader: TextView
    private lateinit var whatToKnowDetail: TextView
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var getStartedButton: MaterialButton
    private lateinit var usePhoneButton: MaterialButton
    private lateinit var useEmailButton: MaterialButton
    private var isEmail = false

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, BottomNav::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        val backButton = findViewById<ImageButton>(R.id.signUpBack)
        firstNameEditText = findViewById(R.id.signUpFirstName)
        lastNameEditText = findViewById(R.id.signUpLastName)
        getStartedButton = findViewById(R.id.getStartedButton)
        whatToKnowHeader = findViewById(R.id.whatToKnowText)
        whatToKnowDetail = findViewById(R.id.whatToKnowDetail)
        usePhoneButton = findViewById(R.id.signUpUsingPhone)
        useEmailButton = findViewById(R.id.signUpUsingEmail)

        getStartedButton.isClickable = false
        getStartedButton.isEnabled = false

        mAuth = Firebase.auth

        usePhoneButton.setOnClickListener {
            usePhoneButton.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.mainGreen))
            usePhoneButton.setTextColor(ContextCompat.getColor(baseContext, R.color.white))
            isEmail = false

            // change email button colors
            useEmailButton.setStrokeColorResource(R.color.mainGreen)
            useEmailButton.strokeWidth = 4
            useEmailButton.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
            useEmailButton.setTextColor(ContextCompat.getColor(baseContext, R.color.mainGreen))
            useEmailButton.setStrokeColorResource(R.color.mainGreen)

            whatToKnowHeader.text = resources.getString(R.string.what_to_know_phone)
            whatToKnowDetail.text = resources.getString(R.string.info_about_auth_phone)
        }

        useEmailButton.setOnClickListener {
            useEmailButton.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.mainGreen))
            useEmailButton.setTextColor(ContextCompat.getColor(baseContext, R.color.white))
            isEmail = true

            // change email button colors
            usePhoneButton.setStrokeColorResource(R.color.mainGreen)
            usePhoneButton.strokeWidth = 4
            usePhoneButton.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
            usePhoneButton.setTextColor(ContextCompat.getColor(baseContext, R.color.mainGreen))
            usePhoneButton.setStrokeColorResource(R.color.mainGreen)

            whatToKnowHeader.text = resources.getString(R.string.what_to_know_email)
            whatToKnowDetail.text = resources.getString(R.string.info_about_auth_email)
        }
//
        backButton.setOnClickListener {
            finish()
        }

        getStartedButton.setOnClickListener {
            intent = if (!isEmail) {
                // phone
                Intent(this, SignUpPhone::class.java)

            } else {
                // email
                Intent(this, SignUpEmail::class.java)
            }

            intent.putExtra("firstName", firstNameEditText.text.toString())
            intent.putExtra("lastName", lastNameEditText.text.toString())
            startActivity(intent)
        }

        firstNameEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disableButton(p0.toString(), getStartedButton)
            }

            override fun afterTextChanged(p0: Editable?) {
                disableButton(p0.toString(), getStartedButton)
            }
        })
    }

    private fun disableButton(phoneNumber: String, button: MaterialButton) {

        if (phoneNumber.isNotEmpty()) {
            button.isEnabled = true
            button.isClickable = true
        } else {
            button.isEnabled = false
            button.isClickable = false
        }
    }
}