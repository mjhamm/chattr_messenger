package com.app.message_app_kotlin.bottomnav.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class FriendViewModel: ViewModel() {

    val data = MutableLiveData<String>()

    fun data(item: String) {
        data.value = item
    }

}