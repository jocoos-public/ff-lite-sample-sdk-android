package com.jocoos.flipflop.sample.live

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.databinding.LiveVideosFragmentBinding
import com.jocoos.flipflop.sample.main.VideoInfo
import com.jocoos.flipflop.sample.main.VideoListAdapter
import com.jocoos.flipflop.sample.utils.setMarginBottom
import com.jocoos.flipflop.sample.utils.setMarginTop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveVideosFragment : Fragment() {
    private var _binding: LiveVideosFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StreamingViewModel by viewModels({requireParentFragment().requireParentFragment()})

    private var videoListAdapter: VideoListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = LiveVideosFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.videoList.setMarginTop(insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
            binding.contentContainer.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        videoListAdapter = VideoListAdapter().apply {
            setClickListener(object : VideoListAdapter.ClickListener {
                override fun onClicked(videoInfo: VideoInfo) {
                    viewModel.playLive(videoInfo)
                    findNavController().navigateUp()
                }
            })
        }
        binding.videoList.layoutManager = LinearLayoutManager(requireContext())
        binding.videoList.adapter = videoListAdapter

        binding.next.setOnClickListener {
            it.findNavController().navigateUp()
        }

        loadVideoList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadVideoList() {
        // this is example. you should get video list from FlipFlop Lite server
        lifecycleScope.launch {
            FlipFlopSampleApp.demoService.videoRooms()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        videoListAdapter?.setItems(it.content.filter {
                            it.videoRoomState == "LIVE" && it.liveUrl != null
                        }.map {
                            VideoInfo(it.id, it.channel.id, it.videoRoomState, it.vodState, it.httpFlvPlayUrl ?: it.rtmpPlayUrl ?: it.liveUrl, it.vodUrl, "", it.title, it.liveStartedAt)
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
}