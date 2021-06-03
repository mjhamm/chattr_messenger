package com.app.message_app_kotlin.ui.signup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.message_app_kotlin.PhoneVerification
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.BottomNav
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignUpPhone : AppCompatActivity() {
    val TAG = "SIGNUP PHONE"

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var phoneNumber: EditText
    private lateinit var nextButton: MaterialButton
    private lateinit var progressHolder: FrameLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_phone)

        val backButton = findViewById<ImageButton>(R.id.signUpPhoneBack)
        phoneNumber = findViewById(R.id.phoneEditText)
        nextButton = findViewById(R.id.nextButton)
        progressHolder = findViewById(R.id.signUpPhone_progressHolder)

        nextButton.isEnabled = false
        nextButton.isClickable = false

        mAuth = Firebase.auth

        if (intent != null) {
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
        }

        backButton.setOnClickListener {
            finish()
        }

        nextButton.setOnClickListener {
            showCodeDialog("+1 ${phoneNumber.text}")
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

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
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                val phoneNumberStripped = phoneNumber.text.toString().replace("-", "")

                progressHolder.visibility = View.GONE
                val intent = Intent(baseContext, PhoneVerification::class.java)
                intent.putExtra("verifyID", verificationId)
                intent.putExtra("firstName", firstName)
                intent.putExtra("lastName", lastName)
                intent.putExtra("phoneNumber", phoneNumberStripped)
                intent.putExtra("isSignIn", false)
                startActivity(intent)
            }
        }

        phoneNumber.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formatPhoneNumber(phoneNumber)
            }

            override fun afterTextChanged(p0: Editable?) {
                if (phoneNumber.text.length == 10) {
                    if (!phoneNumber.text.contains("-")) {
                        phoneNumber.setText(StringBuilder(phoneNumber.text).insert(3, "-").toString())
                        phoneNumber.setText(StringBuilder(phoneNumber.text).insert(7, "-").toString())
                    }
                }

                formatPhoneNumber(phoneNumber)
                disableButton(p0.toString(), nextButton)
            }
        })
    }

    private fun disableButton(phoneNumber: String, button: MaterialButton) {

        if (phoneNumber.isNotEmpty()) {
            if (phoneNumber.length == 12 && phoneNumber.contains("-")) {
                button.isEnabled = true
                button.isClickable = true
            }
        } else {
            button.isEnabled = false
            button.isClickable = false
        }
    }

    private fun formatPhoneNumber(phone: EditText) {
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

    private fun showCodeDialog(phoneNumber: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_code_dialog)
        val titleView = dialog.findViewById<TextView>(R.id.title)
        val detail = dialog.findViewById<TextView>(R.id.detail)
        val confirm = dialog.findViewById<MaterialButton>(R.id.confirm_button)
        val cancel = dialog.findViewById<MaterialButton>(R.id.cancel_button)

        titleView.text = phoneNumber
        detail.text = "A verification code will be sent to the above phone number. Is this number correct?"
        confirm.text = "Yes"
        cancel.text = "Edit Number"

        var checkNum = this.phoneNumber.text.toString().replace("-", "")
        checkNum = "+1$checkNum"

        Log.d(TAG, checkNum)

        confirm.setOnClickListener {
            dialog.dismiss()

            progressHolder.visibility = View.VISIBLE

            // check that phone number isn't already in use
            FirebaseFirestore.getInstance().collection("phone")
                .document(checkNum)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            progressHolder.visibility = View.GONE
                            Toast.makeText(baseContext, "Phone number already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            val options = PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(checkNum)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this)                 // Activity (for callback binding)
                                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        }
                    }
                }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
            this.phoneNumber.requestFocus()
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