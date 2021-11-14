package com.example.vartalap.models

import java.util.*

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    var message: String,
    val dateTime: String,
    var date: Date,

    val conversionId: String,
    val conversionName: String,
    val conversionImage: String
)
