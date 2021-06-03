package com.app.message_app_kotlin.ui.signup

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.message_app_kotlin.PhoneVerification
import com.app.message_app_kotlin.R
import com.google.android.material.button.MaterialButton

class SignUpEmail : AppCompatActivity() {

    private lateinit var nextButton: MaterialButton
    private lateinit var emailEditText: EditText

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

        backButton.setOnClickListener {
            finish()
        }

        nextButton.setOnClickListener {
            showCodeDialog(emailEditText.text.toString())
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

    private fun showCodeDialog(emailAddress: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_code_dialog)
        val titleView = dialog.findViewById<TextView>(R.id.title)
        val detail = dialog.findViewById<TextView>(R.id.detail)
        val confirm = dialog.findViewById<MaterialButton>(R.id.confirm_button)
        val cancel = dialog.findViewById<MaterialButton>(R.id.cancel_button)

        titleView.text = emailAddress
        detail.text = "An email verification will be sent to the above email address. Is this email correct?"
        confirm.text = "Yes"
        cancel.text = "Edit Email"

        confirm.setOnClickListener {
            dialog.dismiss()

            // check that email address isn't already in use

            // if in use -- alert user
            // check
            // if not in use --
            val intent = Intent(this, PhoneVerification::class.java)
            startActivity(intent)
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