package com.app.message_app_kotlin

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.message_app_kotlin.bottomnav.BottomNav
import com.app.message_app_kotlin.ui.signup.SignUpProfile
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class PhoneVerification : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var verifyID: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var phoneNumber: String
    private var isSignIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        val backButton: ImageButton = findViewById(R.id.phone_verify_back)
        val noCode: TextView = findViewById(R.id.no_code)
        val verifyEditText1: EditText = findViewById(R.id.verify1)
        val verifyEditText2: EditText = findViewById(R.id.verify2)
        val verifyEditText3: EditText = findViewById(R.id.verify3)
        val verifyEditText4: EditText = findViewById(R.id.verify4)
        val verifyEditText5: EditText = findViewById(R.id.verify5)
        val verifyEditText6: EditText = findViewById(R.id.verify6)
        val numPad1: Button  = findViewById(R.id.add1)
        val numPad2: Button = findViewById(R.id.add2)
        val numPad3: Button = findViewById(R.id.add3)
        val numPad4: Button = findViewById(R.id.add4)
        val numPad5: Button = findViewById(R.id.add5)
        val numPad6: Button = findViewById(R.id.add6)
        val numPad7: Button = findViewById(R.id.add7)
        val numPad8: Button = findViewById(R.id.add8)
        val numPad9: Button = findViewById(R.id.add9)
        val numPad0: Button = findViewById(R.id.add0)
        val numPadBack: ImageButton = findViewById(R.id.backspace)
        val progressHolder: FrameLayout = findViewById(R.id.signIn_progressHolder)

        //var verificationCode: Int
        var currentCode = ""

        mAuth = FirebaseAuth.getInstance()

        if (intent != null) {
            verifyID = intent.getStringExtra("verifyID").toString()
            firstName = intent.getStringExtra("firstName").toString()
            lastName = intent.getStringExtra("lastName").toString()
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
            isSignIn = intent.getBooleanExtra("isSignIn", false)
        }

        backButton.setOnClickListener {
            finish()
        }

        noCode.setOnClickListener {
            // resend code verification
            showCodeDialog(verifyEditText1)
        }

        verifyEditText1.requestFocus()

        verifyEditText1.showSoftInputOnFocus = false
        verifyEditText2.showSoftInputOnFocus = false
        verifyEditText3.showSoftInputOnFocus = false
        verifyEditText4.showSoftInputOnFocus = false
        verifyEditText5.showSoftInputOnFocus = false
        verifyEditText6.showSoftInputOnFocus = false

        Log.d("VERIFY", "current focus: $currentFocus")

        // updates verifyEditText1
        verifyEditText1.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    verifyEditText2.requestFocus()
                    if (verifyEditText1.text.isNotEmpty()) {
                        currentCode = verifyEditText1.text.toString()
                        Log.d("TAG", "current code: $currentCode")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // updates verifyEditText2
        verifyEditText2.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    verifyEditText3.requestFocus()
                    if (verifyEditText2.text.isNotEmpty()) {
                        currentCode += verifyEditText2.text.toString()
                        Log.d("TAG", "current code: $currentCode")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // updates verifyEditText3
        verifyEditText3.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    verifyEditText4.requestFocus()
                    if (verifyEditText3.text.isNotEmpty()) {
                        currentCode += verifyEditText3.text.toString()
                        Log.d("TAG", "current code: $currentCode")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // updates verifyEditText4
        verifyEditText4.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    verifyEditText5.requestFocus()
                    if (verifyEditText4.text.isNotEmpty()) {
                        currentCode += verifyEditText4.text.toString()
                        Log.d("TAG", "current code: $currentCode")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // updates verifyEditText5
        verifyEditText5.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    verifyEditText6.requestFocus()
                    if (verifyEditText5.text.isNotEmpty()) {
                        currentCode += verifyEditText5.text.toString()
                        Log.d("TAG", "current code: $currentCode")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // updates verifyEditText5
        verifyEditText6.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length == 1) {
                    if (verifyEditText6.text.isNotEmpty()) {
                        if (currentCode.length < 6) {
                            currentCode += verifyEditText6.text.toString()
                            Log.d("TAG", "current code: $currentCode")
                            progressHolder.visibility = View.VISIBLE

                            val credential = PhoneAuthProvider.getCredential(verifyID, currentCode)

                            signInWithPhoneAuthCredential(credential, progressHolder)
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        numPad0.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("0")
        }
        numPad1.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("1")
        }
        numPad2.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("2")
        }
        numPad3.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("3")
        }
        numPad4.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("4")
        }
        numPad5.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("5")
        }
        numPad6.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("6")
        }
        numPad7.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("7")
        }
        numPad8.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("8")
        }
        numPad9.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val currentfocus = currentFocus as EditText
            currentfocus.setText("9")
        }

        // remove currentFocused Edittext.text
        numPadBack.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            var currentFocus = currentFocus as EditText
            if (currentFocus != verifyEditText1) {
                val focusDownId = currentFocus.nextFocusDownId
                currentFocus.text = null
                currentFocus = findViewById(focusDownId)
                findViewById<EditText>(focusDownId).requestFocus()
                currentFocus.text = null
            } else {
                currentFocus.text = null
            }

            if (currentCode.length < 6) {
                var currentCodeInt = currentCode.toInt()
                val lastIndex = currentCodeInt % 10
                currentCodeInt -= lastIndex
                currentCode = currentCodeInt.toString()

                Log.d("VERIFY", "current code: $currentCode")
            }
        }
    }

    private fun showCodeDialog(view: EditText) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_code_dialog)
        val titleView = dialog.findViewById<TextView>(R.id.title)
        val detail = dialog.findViewById<TextView>(R.id.detail)
        val confirm = dialog.findViewById<MaterialButton>(R.id.confirm_button)
        val cancel = dialog.findViewById<MaterialButton>(R.id.cancel_button)

        titleView.text = resources.getString(R.string.resend_code_header)
        detail.text = resources.getString(R.string.resend_code_detail)
        confirm.text = "Resend"
        cancel.text = "Cancel"

        confirm.setOnClickListener {
            dialog.dismiss()
            // TODO: resend phone verification
            view.performClick()
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, progressView: FrameLayout) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                // hide progress view
                progressView.visibility = View.GONE

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val user = task.result?.user
                    Toast.makeText(baseContext, "Correct Verification Code", Toast.LENGTH_SHORT).show()

                    if (isSignIn) {
                        val intent = Intent(baseContext, BottomNav::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("uid", user?.uid)
                        startActivity(intent)
                    } else {
                        val intent = Intent(baseContext, SignUpProfile::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("uid", user?.uid)
                        intent.putExtra("firstName", firstName)
                        intent.putExtra("lastName", lastName)
                        intent.putExtra("phoneNumber", phoneNumber)
                        intent.putExtra("isPhone", true)
                        startActivity(intent)
                    }

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(baseContext, "Incorrect Verification Code", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}