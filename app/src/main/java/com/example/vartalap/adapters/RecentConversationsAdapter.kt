package com.example.vartalap.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vartalap.databinding.ListContainerRecentConversionBinding
import com.example.vartalap.models.ChatMessage
import com.example.vartalap.models.User

class RecentConversationsAdapter(private val conversionListener: ConversionClickListener) : RecyclerView.Adapter<RecentConversationsAdapter.ViewHolder>() {

    var chatMessages: List<ChatMessage> = ArrayList()

    fun setData(chatMessages: List<ChatMessage>){
        this.chatMessages = chatMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatMessages[position], conversionListener)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    class ViewHolder(private val binding: ListContainerRecentConversionBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListContainerRecentConversionBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(chatMessage: ChatMessage, conversionListener: ConversionClickListener){
            binding.chatMessage = chatMessage
            binding.conversionClickListener = conversionListener
            binding.executePendingBindings()
        }

    }
}

class ConversionClickListener(val conversionListener: (chatMessage: ChatMessage) -> Unit){
    fun onConversionClick(chatMessage: ChatMessage) = conversionListener(chatMessage)
}