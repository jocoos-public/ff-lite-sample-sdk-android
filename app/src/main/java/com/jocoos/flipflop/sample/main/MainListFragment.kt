package com.jocoos.flipflop.sample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.api.ApiManager
import com.jocoos.flipflop.sample.databinding.MainListFragmentBinding
import com.jocoos.flipflop.sample.live.LiveWatchInfo
import com.jocoos.flipflop.sample.live.StreamingInfo
import com.jocoos.flipflop.sample.live.VodInfo
import com.jocoos.flipflop.sample.utils.IOCoroutineScope
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainListFragment : Fragment() {
    private var _binding: MainListFragmentBinding? = null
    private val binding get() = _binding!!

    private val scope: CoroutineScope = IOCoroutineScope()

    private var streamingInfo: StreamingInfo? = null
    private var videoListAdapter: VideoListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().run {
            streamingInfo = getParcelable(PreferenceManager.KEY_STREAMING_INFO)
        }
        if (streamingInfo == null) {
            Toast.makeText(requireContext(), "need live info", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        videoListAdapter = VideoListAdapter().apply {
            setClickListener(object : VideoListAdapter.ClickListener {
                override fun onClicked(videoInfo: VideoInfo) {
                    if (videoInfo.videoRoomState == "LIVE") {
                        watchLive(videoInfo)
                    } else {
                        playVod(videoInfo)
                    }
                }
            })
        }
        binding.videoList.layoutManager = LinearLayoutManager(requireContext())
        binding.videoList.adapter = videoListAdapter

        loadVideoList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadVideoList() {
        scope.launch {
            val videoRooms = ApiManager.getInstance().getVideoRooms()

            withContext(Dispatchers.Main) {
                videoListAdapter?.setItems(videoRooms.content.filter {
                    it.videoRoomState == "LIVE" || (it.videoRoomState == "ENDED" && (it.vodState == "ARCHIVED"))
                }.map {
                    VideoInfo(it.videoRoomState, it.vodState, it.liveUrl, it.vodUrl, it.member.appUserName, it.title, it.chat.channelKey, it.liveStartedAt)
                })
                videoListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun watchLive(videoInfo: VideoInfo) {
        val liveWatchInfo = LiveWatchInfo(
            liveUrl = videoInfo.liveUrl!!,
            userId = streamingInfo!!.userId,
            userName = streamingInfo!!.userName,
            chatToken = streamingInfo!!.chatToken,
            chatAppId = streamingInfo!!.chatAppId,
            channelKey = videoInfo.channelKey,
        )
        val bundle = Bundle().apply {
            putParcelable(PreferenceManager.KEY_LIVE_WATCH_INFO, liveWatchInfo)
        }
        findNavController().navigate(R.id.watchLive, bundle)
    }

    private fun playVod(videoInfo: VideoInfo) {
        val vodInfo = VodInfo(
            userId = streamingInfo!!.userId,
            vodUrl = videoInfo.vodUrl!!,
            chatToken = streamingInfo!!.chatToken,
            chatAppId = streamingInfo!!.chatAppId,
            channelKey = videoInfo.channelKey,
            liveStartedAt = videoInfo.createdAt!!,
        )
        val bundle = Bundle().apply {
            putParcelable(PreferenceManager.KEY_VOD_INFO, vodInfo)
        }
        findNavController().navigate(R.id.playVod, bundle)
    }
}
