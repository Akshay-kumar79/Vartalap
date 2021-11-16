package com.example.vartalap.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vartalap.databinding.ItemContainerReceivedMessageBinding
import com.example.vartalap.databinding.ItemContainerSentMessageBinding
import com.example.vartalap.models.ChatMessage

class ChatAdapter(private val senderId: String, private var receiverProfileImage: Bitmap?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    private var chatMessages: List<ChatMessage> = ArrayList()

    fun setData(chats: List<ChatMessage>) {
        chatMessages = chats
        notifyDataSetChanged()
    }

    fun setReceivedProfileImage(bitmap: Bitmap){
        receiverProfileImage = bitmap
        notifyItemRangeChanged(0, chatMessages.size)
    }

    fun insertData(chats: List<ChatMessage>){
        chatMessages = chats
        notifyItemRangeInserted(chats.size, chats.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == senderId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT){
            SentMessageViewHolder.from(parent)
        }else{
            ReceivedMessageViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_SENT)
            (holder as SentMessageViewHolder).bind(chatMessages[position])
        else
            (holder as ReceivedMessageViewHolder).bind(chatMessages[position], receiverProfileImage)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }


    class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object{
            fun from(parent: ViewGroup): SentMessageViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemContainerSentMessageBinding.inflate(inflater, parent, false)
                return SentMessageViewHolder(binding)
            }
        }

        fun bind(chatMessage: ChatMessage){
            binding.chatMessage = chatMessage
            binding.executePendingBindings()
        }

    }

    class ReceivedMessageViewHolder(private val binding: ItemContainerReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object{
            fun from(parent: ViewGroup): ReceivedMessageViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemContainerReceivedMessageBinding.inflate(inflater, parent, false)
                return ReceivedMessageViewHolder(binding)
            }
        }

        fun bind(chatMessage: ChatMessage, receiverProfileImage: Bitmap?){
            binding.chatMessage = chatMessage
            if (receiverProfileImage != null)
            binding.profileImageView.setImageBitmap(receiverProfileImage)
            binding.executePendingBindings()
        }

    }

}