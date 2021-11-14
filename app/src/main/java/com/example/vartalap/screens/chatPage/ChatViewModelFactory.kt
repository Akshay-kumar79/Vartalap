package com.example.vartalap.screens.chatPage

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vartalap.models.User
import java.lang.IllegalArgumentException

class ChatViewModelFactory(private val user: User, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)){
            return ChatViewModel(user, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}