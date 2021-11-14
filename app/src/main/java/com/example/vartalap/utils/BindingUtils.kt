package com.example.vartalap.utils

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vartalap.adapters.ChatAdapter
import com.example.vartalap.adapters.RecentConversationsAdapter
import com.example.vartalap.adapters.UsersAdapter
import com.example.vartalap.models.ChatMessage
import com.example.vartalap.models.User
import com.example.vartalap.screens.chatPage.ChatViewModel


@BindingAdapter("setDecodedImage")
fun setDecodedImage(imageView: ImageView, encodedImage: String){
    val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    imageView.setImageBitmap(bitmap)
}


// select user page
@BindingAdapter("addUsersList")
fun addUsersList(recyclerView: RecyclerView, data: List<User>?){
    val adapter = recyclerView.adapter as UsersAdapter
    if (data != null) {
        adapter.setData(data)
    }
}

//chat page
@BindingAdapter("addChatList","chatViewModel", requireAll = true)
fun addChatList(recyclerView: RecyclerView, data: List<ChatMessage>?, viewModel: ChatViewModel){
    val adapter = recyclerView.adapter as ChatAdapter
    if(data!= null){
        if(viewModel.messageInsertMode == Constants.MODE_SET_DATA) {
            adapter.setData(data)
        }else{
            adapter.insertData(data)
            recyclerView.smoothScrollToPosition(data.size - 1)
        }
    }
}

//main page
@BindingAdapter("addConversionList")
fun addConversionList(recyclerView: RecyclerView, data: List<ChatMessage>?){
    val adapter = recyclerView.adapter as RecentConversationsAdapter
    if (data != null){
        adapter.setData(data)
        recyclerView.smoothScrollToPosition(0)
    }
}