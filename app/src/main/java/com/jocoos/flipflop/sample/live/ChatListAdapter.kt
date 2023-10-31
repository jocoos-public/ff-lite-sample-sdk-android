package com.jocoos.flipflop.sample.live

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.sample.databinding.ChatListItemBinding
import java.util.Date

data class ChatItem(
    val userId: String,
    val username: String,
    val message: String,
    val messageId: String,
    val createdAt: Date? = Date(),
)

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ChatItemViewHolder>() {
    private var items = mutableListOf<ChatItem>()

    fun add(chat: ChatItem) {
        items.add(chat)
        notifyItemInserted(items.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val binding = ChatListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatItemViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    inner class ChatItemViewHolder(private val binding: ChatListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: ChatItem) {
            binding.chatUsername.text = item.username
            binding.chatMessage.text = item.message
        }
    }
}
