package com.app.message_app_kotlin.models

data class User(
    val uid: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String = "",
    val phone: String = ""
)