package com.app.message_app_kotlin

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.app.message_app_kotlin.bottomnav.BottomNav
import com.app.message_app_kotlin.forgot.ForgotPassword
import com.app.message_app_kotlin.ui.signup.SignUp
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignIn : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            val intent = Intent(this, BottomNav::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        val logo = findViewById<CardView>(R.id.appLogo)
        val phoneEditText = findViewById<EditText>(R.id.signInPhone)
        val passwordEditText = findViewById<EditText>(R.id.signInPassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val signInButton = findViewById<MaterialButton>(R.id.signInButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)
        val methodSwitchText: TextView = findViewById(R.id.switchMethodText)
        var isEmail = false

        mAuth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("SIGN IN", "onVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("SIGN IN", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("SIGN IN", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                val phoneNumberStripped = phoneEditText.text.toString().replace("-", "")

                //progressHolder.visibility = View.GONE
                val intent = Intent(baseContext, PhoneVerification::class.java)
                intent.putExtra("verifyID", verificationId)
                intent.putExtra("phoneNumber", phoneNumberStripped)
                intent.putExtra("isSignIn", true)
                startActivity(intent)
            }
        }

        signInButton.isEnabled = false
        signInButton.isClickable = false
        forgotPassword.visibility = View.GONE
        passwordEditText.visibility = View.GONE

        changeSignInMethod(isEmail, phoneEditText, passwordEditText, methodSwitchText)

        methodSwitchText.setOnClickListener {
            Log.d("SIGN IN", "$isEmail")
            if (isEmail) {
                isEmail = false
                changeSignInMethod(isEmail, phoneEditText, passwordEditText, methodSwitchText)
                forgotPassword.visibility = View.GONE
                passwordEditText.visibility = View.GONE
            } else {
                isEmail = true
                changeSignInMethod(isEmail, phoneEditText, passwordEditText, methodSwitchText)
                forgotPassword.visibility = View.VISIBLE
                passwordEditText.visibility = View.VISIBLE
            }
        }

        signInButton.setOnClickListener {
            if (isEmail) {
                val email = phoneEditText.text.toString()
                val password = passwordEditText.text.toString()
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val intent = Intent(this, BottomNav::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(baseContext, it.message, Toast.LENGTH_SHORT).show()
                    }
            } else {

                var checkNum = phoneEditText.text.toString().replace("-", "")
                checkNum = "+1$checkNum"

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(checkNum)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        phoneEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isEmail) {
                    formatPhoneNumber(isEmail, phoneEditText)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!isEmail) {
                    if (phoneEditText.text.length > 11 && !phoneEditText.text.contains("-")) {
                        val formatted =
                            phoneEditText.text.toString().substring(2, phoneEditText.text.length)
                        Log.d("SIGNIN", formatted)
                        phoneEditText.setText(StringBuilder(formatted).insert(3, "-").toString())
                        phoneEditText.setText(StringBuilder(formatted).insert(7, "-").toString())
                        phoneEditText.setText(formatted)
                    }
                    if (phoneEditText.text.length == 10) {
                        if (!phoneEditText.text.contains("-")) {
                            phoneEditText.setText(
                                StringBuilder(phoneEditText.text).insert(3, "-").toString()
                            )
                            phoneEditText.setText(
                                StringBuilder(phoneEditText.text).insert(7, "-").toString()
                            )
                        }
                    }

                    formatPhoneNumber(isEmail, phoneEditText)
                }

                disableButton(isEmail, phoneEditText.text.toString(), passwordEditText.text.toString(), signInButton)
            }
        })

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                disableButton(isEmail, phoneEditText.text.toString(), passwordEditText.text.toString(), signInButton)
            }

            override fun afterTextChanged(p0: Editable?) {
                disableButton(isEmail, phoneEditText.text.toString(), passwordEditText.text.toString(), signInButton)
            }

        })
    }

    private fun changeSignInMethod(isEmail: Boolean, phone: EditText, password: EditText, signInTextView: TextView) {

        if (isEmail) {
            signInTextView.text = "Sign in with Phone"
            phone.hint = "Email Address"
            phone.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            phone.filters = arrayOf<InputFilter>(LengthFilter(100))
            phone.text = null
            password.text = null
        } else {
            signInTextView.text = "Sign in with Email"
            phone.hint = "000-000-0000"
            phone.inputType = InputType.TYPE_CLASS_PHONE
            phone.keyListener = DigitsKeyListener.getInstance("0123456789")
            phone.filters = arrayOf<InputFilter>(LengthFilter(12))
            phone.text = null
            password.text = null
        }
    }

    private fun formatPhoneNumber(isEmail: Boolean, phone: EditText) {

        if (!isEmail) {
            val text: String = phone.text.toString()
            val textlength: Int = phone.text.length

            if (text.endsWith(" ")) return

            if (textlength > 3) {
                if (text.substring(0,3) != "111") {
                    if (textlength == 4) {
                        if (!text.contains("-")) {
                            phone.setText(StringBuilder(text).insert(text.length - 1, "-").toString())
                            phone.setSelection(phone.text.length)
                        }
                    } else if (textlength == 8) {
                        if (text.substring(7,8) != "-") {
                            phone.setText(StringBuilder(text).insert(text.length - 1, "-").toString())
                            phone.setSelection(phone.text.length)
                        }
                    }
                }
            }
        }
    }

    private fun sendVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun disableButton(isEmail: Boolean, email: String, password: String, button: MaterialButton) {

        if (isEmail) {
            if (email.contains("@") && password.isNotEmpty()) {
                button.isEnabled = true
                button.isClickable = true
            } else {
                button.isEnabled = false
                button.isClickable = false
            }
        } else {
            if (email.length == 12 && email.contains("-")) {
                button.isEnabled = true
                button.isClickable = true
            } else {
                button.isEnabled = false
                button.isClickable = false
            }
        }
    }
}