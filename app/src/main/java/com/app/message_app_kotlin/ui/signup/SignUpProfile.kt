package com.app.message_app_kotlin.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.BottomNav
import com.app.message_app_kotlin.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpProfile : AppCompatActivity() {

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var uid: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var confirmPass: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressHolder: FrameLayout
    private var isPhoneSignUp: Boolean = false
    private lateinit var phoneNumber: String
    private lateinit var emailAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_profile)

        val backButton: ImageButton = findViewById(R.id.signUpProfileBack)
        val signUpFinishButton: MaterialButton = findViewById(R.id.signUpFinishButton)
        val userNameEditText: EditText = findViewById(R.id.signUpUsername)
        val passEditText: EditText = findViewById(R.id.signUpPassword)
        val passConfirmEditText: EditText = findViewById(R.id.signUpPasswordConfirm)
        val uniqueUserName: TextView = findViewById(R.id.uniqueUsernameText)
        val passwordMatch: TextView = findViewById(R.id.passwordMatch)
        progressHolder = findViewById(R.id.signUpProfile_progressHolder)

        mAuth = FirebaseAuth.getInstance()

        backButton.setOnClickListener {
            finish()
        }

        signUpFinishButton.setOnClickListener {
            checkUniqueUsername(userNameEditText.text.toString(), uniqueUserName)
        }

        if (intent != null) {
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
            uid = intent.getStringExtra("uid").toString()
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
            emailAddress = intent.getStringExtra("email").toString()
            isPhoneSignUp = intent.getBooleanExtra("isPhone", false)
        }

        if (isPhoneSignUp) {
            passEditText.visibility = View.GONE
            passConfirmEditText.visibility = View.GONE
        } else {
            passEditText.visibility = View.VISIBLE
            passConfirmEditText.visibility = View.VISIBLE
        }

        passEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disableButton(userNameEditText, passEditText, passConfirmEditText, signUpFinishButton, passwordMatch)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        passConfirmEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disableButton(userNameEditText, passEditText, passConfirmEditText, signUpFinishButton, passwordMatch)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun disableButton(username: EditText, password: EditText, passConfirm: EditText, button: MaterialButton, passMatch: TextView) {
        if (username.text.isNotEmpty()) {
            if (password.text.equals(passConfirm.text)) {
                passMatch.visibility = View.GONE
                button.isClickable = true
                button.isEnabled = true
            } else {
                passMatch.visibility = View.VISIBLE
                button.isClickable = false
                button.isEnabled = false
            }
        } else {
            button.isClickable = false
            button.isEnabled = false
        }
    }

    private fun checkUniqueUsername(username: String, isTaken: TextView) {
        progressHolder.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("usernames")
            .document(username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        progressHolder.visibility = View.GONE
                        isTaken.visibility = View.VISIBLE
                    } else {

                        // adds username to database to make checking if username is unique easier
                        val mapUsername: Map<String, String> = mapOf("username" to username)
                        FirebaseFirestore.getInstance().collection("usernames").document(username)
                            .set(mapUsername)

                        if (isPhoneSignUp) {
                            // adds phonenumber to database to make checking if user is already created with that number
                            val mapPhone: Map<String, String> = mapOf("phone" to phoneNumber)
                            FirebaseFirestore.getInstance().collection("phone")
                                .document(phoneNumber)
                                .set(mapPhone)
                        } else {
                            // adds email address to database to make checking if user is already created with that email
                            val mapEmail: Map<String, String> = mapOf("email" to emailAddress)
                            FirebaseFirestore.getInstance().collection("phone").document(emailAddress)
                                .set(mapEmail)
                        }

                        // add user to firebase with name, uid, phone number/email and username information
                        saveUserToFirebase(uid, username, firstName, lastName, emailAddress, phoneNumber)

                        progressHolder.visibility = View.GONE
                    }
                }
            }

    }

    private fun saveUserToFirebase(uid: String, username: String, firstName: String, lastName: String, email: String, phone: String) {

        val user = User(uid, username, firstName, lastName, email, phone)

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("SIGN UP", "addedUserToFirebase:success")
                val intent = Intent(this, BottomNav::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()

            }
    }
}