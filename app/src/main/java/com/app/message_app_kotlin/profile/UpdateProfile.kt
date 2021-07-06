package com.app.message_app_kotlin.profile

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.app.message_app_kotlin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfile : AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var updateProfileButton: MaterialButton
    private var firstName: String? = null
    private var lastName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val mode = applicationContext?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        when(mode) {
            Configuration.UI_MODE_NIGHT_YES -> window.statusBarColor = getColor(R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> window.statusBarColor = getColor(R.color.white)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> window.statusBarColor = getColor(R.color.white)
            else -> window.statusBarColor = getColor(R.color.white)
        }

        if (mode == Configuration.UI_MODE_NIGHT_NO || mode == Configuration.UI_MODE_NIGHT_UNDEFINED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val backButton: ImageButton = findViewById(R.id.updateProfile_back)
        updateProfileButton = findViewById(R.id.update_profile_button)

        updateProfileButton.isEnabled = false

        firstNameEditText = findViewById(R.id.firstname_editText)
        lastNameEditText = findViewById(R.id.lastname_editText)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            uid = mAuth.currentUser!!.uid
        }

        firstNameEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                updateProfileButton.isEnabled = checkChangedName(p0.toString(), lastNameEditText.text.toString())
            }

        })

        lastNameEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                updateProfileButton.isEnabled = checkChangedName(firstNameEditText.text.toString(), p0.toString())
            }

        })

        updateProfileButton.setOnClickListener {
            if (firstNameEditText.text.toString().isNotEmpty()) {
                updateProfileInfo(
                    uid,
                    firstNameEditText.text.toString(),
                    lastNameEditText.text.toString()
                )
            } else {
                Snackbar.make(it, "You must enter a First Name", Snackbar.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        getNameInfo(uid)
    }

    private fun checkChangedName(firstName: String?, lastName: String?) : Boolean {

        return this.firstName != firstName || this.lastName != lastName
    }

    private fun getNameInfo(uid: String) {
        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { user ->
                val firstName: String? = user.getString("firstName")
                val lastName: String? = user.getString("lastName")

                this.firstName = firstName
                this.lastName = lastName

                // sets edittexts and profile image
                setUserInfo(firstName, lastName)
            }
    }

    private fun updateProfileInfo(uid: String, firstName: String?, lastName: String?) {

        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .update("firstName", firstName)
            .addOnSuccessListener {
                this.firstName = firstName.toString()
                updateProfileButton.isEnabled = false
                if (this.lastName != lastName) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .update("lastName", lastName)
                        .addOnSuccessListener {
                            this.lastName = lastName.toString()
                            Snackbar.make(updateProfileButton, "Your profile has been updated", Snackbar.LENGTH_SHORT).show()
                        }
                } else {
                    Snackbar.make(updateProfileButton, "Your profile has been updated", Snackbar.LENGTH_SHORT).show()
                }

                this.window.currentFocus?.clearFocus()
                // TODO: CLOSE KEYBOARD
            }
    }

    private fun setUserInfo(firstName: String?, lastName: String?) {
        firstNameEditText.setText(firstName)
        if (lastName != null) lastNameEditText.setText(lastName)
    }
}