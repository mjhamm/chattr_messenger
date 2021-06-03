package com.app.message_app_kotlin.bottomnav.ui.messages

data class Message(
        val uid: String,
        val image: String,
        val firstName: String,
        var lastName: String,
        val userName: String,
        var message: String,
        var time: String,
        var notifications: Int
)