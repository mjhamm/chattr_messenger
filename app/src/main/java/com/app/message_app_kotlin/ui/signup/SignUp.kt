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

//    private lateinit var filter: InputFilter
    private lateinit var mAuth: FirebaseAuth
//    private lateinit var usernameEditText: EditText
//    private lateinit var nameEditText: EditText
//    private lateinit var emailEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var passConfirmEditText: EditText
//    private lateinit var signUpButton: MaterialButton
//    private lateinit var passMatch: TextView
//    private lateinit var usernameTaken: TextView
//    private lateinit var switch: SwitchMaterial
//    private lateinit var switchText: TextView
    private lateinit var whatToKnowHeader: TextView
    private lateinit var whatToKnowDetail: TextView
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var getStartedButton: MaterialButton
    private lateinit var usePhoneButton: MaterialButton
    private lateinit var useEmailButton: MaterialButton
    //private lateinit var methodSwitch: SwitchCompat
    private var isEmail = false

    override fun onStart() {
        super.onStart()

        mAuth.signOut()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, BottomNav::class.java)
            startActivity(intent)
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
        //methodSwitch = findViewById(R.id.signUpMethodSwitch)
        whatToKnowHeader = findViewById(R.id.whatToKnowText)
        whatToKnowDetail = findViewById(R.id.whatToKnowDetail)
        usePhoneButton = findViewById(R.id.signUpUsingPhone)
        useEmailButton = findViewById(R.id.signUpUsingEmail)

        getStartedButton.isClickable = false
        getStartedButton.isEnabled = false

//        usernameEditText = findViewById(R.id.signUpUsername)
//        nameEditText = findViewById(R.id.signUpName)
//        emailEditText = findViewById(R.id.signUpEmail)
//        passwordEditText = findViewById(R.id.signUpPassword)
//        passConfirmEditText = findViewById(R.id.signUpPasswordConfirm)
//        signUpButton = findViewById(R.id.signUpButton)
//        passMatch = findViewById(R.id.passMatch)
//        usernameTaken = findViewById(R.id.usernameTaken)
//        switchText = findViewById(R.id.switchText)
//        switch = findViewById(R.id.signUpSwitch)
//
//        signUpButton.isEnabled = false
//        signUpButton.isClickable = false
//        passMatch.visibility = View.GONE
//        usernameTaken.visibility = View.GONE
//
//        switchText.text = "Email"
//
        mAuth = Firebase.auth

//        methodSwitch.setOnCheckedChangeListener { _, b ->
//
//            if (b) {
//                isEmail = true
//                whatToKnowHeader.text = resources.getString(R.string.what_to_know_email)
//                whatToKnowDetail.text = resources.getString(R.string.info_about_auth_email)
//            } else {
//                isEmail = false
//                whatToKnowHeader.text = resources.getString(R.string.what_to_know_phone)
//                whatToKnowDetail.text = resources.getString(R.string.info_about_auth_phone)
//            }
//            Log.d("ISEMAIL", "$isEmail")
//        }

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
//
//        // switch change
//        switch.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                switchText.text = "Phone"
//                emailEditText.hint = "Phone"
//                emailEditText.inputType = InputType.TYPE_CLASS_PHONE
//            } else {
//                switchText.text = "Email"
//                emailEditText.hint = "Email"
//                emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            }
//            emailEditText.text = null
//        }
//
//        usernameEditText.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//        })
//
//        nameEditText.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//        })
//
//        emailEditText.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//        })
//
//        passwordEditText.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//        })
//
//        passConfirmEditText.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                disableButton(usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString(), passConfirmEditText.text.toString())
//            }
//        })
//
//
//    }
//
//    private fun registerUser() {
//        val username = usernameEditText.text.toString()
//        val name = nameEditText.text.toString()
//        val email = emailEditText.text.toString()
//        val password = passwordEditText.text.toString()
//        val passConfirm = passConfirmEditText.text.toString()
//
//        if (username.isEmpty()) {
//            Toast.makeText(baseContext, "Please enter a username to Sign Up", Toast.LENGTH_SHORT).show()
//        } else {
//            if (name.isEmpty()) {
//                Toast.makeText(baseContext, "Please enter your name to Sign Up", Toast.LENGTH_SHORT).show()
//            } else {
//                if (email.isEmpty()) {
//                    Toast.makeText(
//                        baseContext,
//                        "Please enter an email to Sign Up",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    if (password.isEmpty()) {
//                        Toast.makeText(
//                            baseContext,
//                            "Please enter a password to Sign Up",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        if (password != passConfirm) {
//                            Toast.makeText(
//                                baseContext,
//                                "Passwords do not match",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            FirebaseFirestore.getInstance().collection("usernames")
//                                .document(username)
//                                .get()
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        val document = task.result
//                                        if (document != null && document.exists()) {
//                                            usernameTaken.visibility = View.VISIBLE
//                                        } else {
//                                            val map: Map<String, String> = mapOf("username" to username)
//                                            FirebaseFirestore.getInstance().collection("usernames").document(username)
//                                                .set(map)
//                                            mAuth.createUserWithEmailAndPassword(email, password)
//                                                .addOnCompleteListener {
//                                                    if (it.isSuccessful) {
//                                                        //Sign in success
//                                                        Log.d("SIGN UP", "createUserWithEmail:success")
//                                                        saveUserToFirebase()
//                                                    } else {
//                                                        // Sign in fails
//                                                        Log.w("SIGN UP", "createUserWithEmail:failure", it.exception
//                                                        )
//                                                        //Toast.makeText(baseContext, "Sign Up failed. Please try again", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                }
//                                                .addOnFailureListener {
//                                                    Toast.makeText(baseContext, it.message, Toast.LENGTH_SHORT).show()
//                                                }
//                                        }
//                                    }
//                                }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun saveUserToFirebase() {
//        val uid = FirebaseAuth.getInstance().uid ?: return
//
//        val user = User(uid, usernameEditText.text.toString(), nameEditText.text.toString(), emailEditText.text.toString())
//
//        FirebaseFirestore.getInstance().collection("users").document(uid)
//            .set(user)
//            .addOnSuccessListener {
//                Log.d("SIGN UP", "addedUserToFirebase:success")
//                val intent = Intent(this, BottomNav::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                finish()
//
//            }
//    }