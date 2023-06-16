package com.jocoos.flipflop.sample.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jocoos.flipflop.sample.databinding.ChatEffectListItemBinding
import com.jocoos.flipflop.sample.utils.OnRecyclerViewItemClickListener

class ChatEffect(val effectResId: Int)

class ChatEffectListAdapter(
    private val chatEffectList: List<ChatEffect>,
    private val onItemClickListener: OnRecyclerViewItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ChatEffectListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatEffectListViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatEffectListViewHolder).bind(chatEffectList[position])
    }

    override fun getItemCount(): Int {
        return chatEffectList.size
    }

    private class ChatEffectListViewHolder(
        private val binding: ChatEffectListItemBinding,
        private val onItemClickListener: OnRecyclerViewItemClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.invoke(itemView, position)
                }
            }
        }

        fun bind(chatEffect: ChatEffect) {
            Glide.with(itemView)
                .load(chatEffect.effectResId)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.imageThumbnail)
        }
    }
}