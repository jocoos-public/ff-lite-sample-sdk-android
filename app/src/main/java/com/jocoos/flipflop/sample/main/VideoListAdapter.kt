package com.jocoos.flipflop.sample.main

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.MainVideoItemBinding
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoInfo(
    val videoRoomId: Long,
    val channelId: Long,
    val videoRoomState: String,
    val vodState: String,
    val liveUrl: String?,
    val vodUrl: String?,
    val username: String,
    val title: String,
    val createdAt: String?
): Parcelable

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private var items: List<VideoInfo> = emptyList()
    private var listener: ClickListener? = null
    interface ClickListener {
        fun onClicked(videoInfo: VideoInfo)
    }

    fun setItems(videos: List<VideoInfo>) {
        items = videos
    }

    fun setClickListener(listener: ClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainVideoItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            listener?.onClicked(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: MainVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(videoInfo: VideoInfo) {
            binding.thumbnail.setImageResource(R.drawable.group_6)
            binding.profileUrl.setImageResource(R.drawable.group_5)

            binding.textLive.isVisible = videoInfo.videoRoomState == "LIVE"

            binding.username.text = videoInfo.username
            binding.title.text = videoInfo.title
            binding.createdAt.text = videoInfo.createdAt
        }
    }
}