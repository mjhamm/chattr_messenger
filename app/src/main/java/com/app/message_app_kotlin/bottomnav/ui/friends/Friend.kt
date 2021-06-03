package com.app.message_app_kotlin.bottomnav.ui.friends

data class Friend(
        val viewType: Int,
        val uid: String,
        val image: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        var isOnline: Boolean = false,
        var top: Boolean = false,
)