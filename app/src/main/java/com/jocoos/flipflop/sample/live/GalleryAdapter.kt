package com.jocoos.flipflop.sample.live

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jocoos.flipflop.sample.databinding.ImageListItemBinding
import com.jocoos.flipflop.sample.utils.px

class GalleryAdapter(
    private var selectedItemPosition: Int = -1,
    private val onItemClickListener: OnItemClickListener? = null
) : PagingDataAdapter<Uri, RecyclerView.ViewHolder>(diffCallback) {
    interface OnItemClickListener {
        fun onFileClicked(uri: Uri, position: Int)
        fun onCheckClicked(uri: Uri, prevPosition: Int, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedItem(position: Int) {
        selectedItemPosition = if (selectedItemPosition == position) {
            -1
        } else {
            position
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ImageListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FileListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            (holder as FileListViewHolder).bind(item, selectedItemPosition == position)
            holder.itemView.setOnClickListener {
                onItemClickListener?.onCheckClicked(item, selectedItemPosition, position)
            }
        }

        when (position % 8) {
            0,1,2,3 -> {
                holder.itemView.setPadding(1.px, 1.px, 1.px, 2.px)
            }
            4,5,6,7 -> {
                holder.itemView.setPadding(1.px, 1.px, 1.px, 20.px)
            }
        }
    }

    private class FileListViewHolder(
        val binding: ImageListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, isSelected: Boolean = false) {
            Glide.with(binding.root)
                .load(uri)
                .into(binding.imageThumbnail)

            binding.fileCheck.isVisible = isSelected
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
            }
        }
    }
}
