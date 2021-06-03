package com.app.message_app_kotlin.forgot

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.app.message_app_kotlin.R

class ForgotPassword: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blue)

        val back: ImageButton = findViewById(R.id.forgotBack)

        back.setOnClickListener {
            finish()
        }
    }
}