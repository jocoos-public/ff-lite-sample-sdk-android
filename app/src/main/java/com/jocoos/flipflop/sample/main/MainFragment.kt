package com.jocoos.flipflop.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.preferenceManager
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.api.ApiManager
import com.jocoos.flipflop.sample.databinding.MainFragmentBinding
import com.jocoos.flipflop.sample.live.StreamingActivity
import com.jocoos.flipflop.sample.live.StreamingInfo
import com.jocoos.flipflop.sample.utils.IOCoroutineScope
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment: Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val scope: CoroutineScope = IOCoroutineScope()

    private var appUserId: String = ""
    private var streamKey: String = ""
    private var chatToken: String = ""
    private var chatAppId: String = ""
    private var streamingInfo: StreamingInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.streaming.setOnClickListener {
            showStreaming()
        }
        binding.streamingView.setOnClickListener {
            showVideoList()
        }

        scope.launch {
            // WARN: this is a sample implementation so that you can understand
            // how to implement your server which is connected with FlipFlop Lite
            // DO NOT USE THIS CODE DIRECTLY ON YOUR IMPLEMENTATION
            with(ApiManager.getInstance()) {
                // auth login
                val response = login(preferenceManager.username, preferenceManager.password)
                appUserId = response.fflTokens.member.appUserId

                // get stream key
                val response2 = streamKey(response.fflTokens.member.id)
                streamKey = response2?.streamKey ?: ""

                // get chat token
                val response3 = chatToken()
                chatToken = response3.chatToken
                chatAppId = response3.appId

                streamingInfo = StreamingInfo(response.fflTokens.member.appUserId, response2?.streamKey ?: "", response3.chatToken, response3.appId, response3.userId, response3.userName)
            }
        }
    }

    private fun showStreaming() {
        if (streamingInfo == null) {
            Toast.makeText(requireContext(), "need to get info for live streaming", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireActivity(), StreamingActivity::class.java).apply {
            putExtra(PreferenceManager.KEY_STREAMING_INFO, streamingInfo)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showVideoList() {
        if (streamingInfo == null) {
            Toast.makeText(requireContext(), "need to get streaming info", Toast.LENGTH_SHORT).show()
            return
        }

        findNavController().navigate(R.id.videoList, Bundle().apply {
            putParcelable(PreferenceManager.KEY_STREAMING_INFO, streamingInfo)
        })
    }
}
