package com.jocoos.flipflop.sample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.MainListFragmentBinding
import com.jocoos.flipflop.sample.live.LiveWatchInfo
import com.jocoos.flipflop.sample.live.StreamingInfo
import com.jocoos.flipflop.sample.live.VodInfo
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainListFragment : Fragment() {
    private var _binding: MainListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
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
            accessToken = getString(PreferenceManager.KEY_ACCESS_TOKEN) ?: ""
        }
        if (accessToken.isEmpty()) {
            Toast.makeText(requireContext(), "access token should not be empty", Toast.LENGTH_SHORT).show()
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

    /**
     * UPDATE
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun loadVideoList() {
        // this is example. you should get video list from FlipFlop Lite server
        lifecycleScope.launch {
            FlipFlopSampleApp.demoService.videoRooms()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        videoListAdapter?.setItems(it.content.map {
                            VideoInfo(it.id, it.channel.id, it.videoRoomState, it.vodState, it.liveUrl, it.vodUrl, "", it.title, it.liveStartedAt)
                        })
                        videoListAdapter?.notifyDataSetChanged()
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun watchLive(videoInfo: VideoInfo) {
        val liveWatchInfo = LiveWatchInfo(
            videoRoomId = videoInfo.videoRoomId,
            channelId = videoInfo.channelId,
            liveUrl = videoInfo.liveUrl!!,
            userId = FlipFlopSampleApp.preferenceManager.userId,
            userName = FlipFlopSampleApp.preferenceManager.username,
        )
        val bundle = Bundle().apply {
            putString(PreferenceManager.KEY_ACCESS_TOKEN, accessToken)
            putParcelable(PreferenceManager.KEY_LIVE_WATCH_INFO, liveWatchInfo)
        }
        findNavController().navigate(R.id.watchLive, bundle)
    }

    private fun playVod(videoInfo: VideoInfo) {
        if (videoInfo.vodUrl == null) {
            return
        }

        val vodInfo = VodInfo(
            videoRoomId = videoInfo.videoRoomId,
            channelId = videoInfo.channelId,
            userId = FlipFlopSampleApp.preferenceManager.userId,
            vodUrl = videoInfo.vodUrl,
            liveStartedAt = videoInfo.createdAt!!,
        )
        val bundle = Bundle().apply {
            putString(PreferenceManager.KEY_ACCESS_TOKEN, accessToken)
            putParcelable(PreferenceManager.KEY_VOD_INFO, vodInfo)
        }
        findNavController().navigate(R.id.playVod, bundle)
    }
}
