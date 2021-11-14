package com.example.vartalap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vartalap.databinding.ListContainerUserBinding
import com.example.vartalap.models.User

class UsersAdapter(private val userClickListener: UserClickListener): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var users: List<User> = ArrayList()
    fun setData(users: List<User>){
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position], userClickListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(private val binding: ListContainerUserBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListContainerUserBinding.inflate(inflater, parent,false)
                return ViewHolder(binding)
            }
        }

        fun bind(user: User, userClickListener: UserClickListener){
            binding.user = user
            binding.userClickListener = userClickListener
            binding.executePendingBindings()
        }
    }
}

class UserClickListener(val clickListener: (user: User) -> Unit){
    fun onClick(user: User) = clickListener(user)
}