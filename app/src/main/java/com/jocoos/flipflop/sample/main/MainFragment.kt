package com.jocoos.flipflop.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.MainFragmentBinding
import com.jocoos.flipflop.sample.live.StreamingActivity
import com.jocoos.flipflop.sample.utils.PreferenceManager

/**
 * need to get access token for streaming
 * : refer to the SDK documentation
 */
class MainFragment: Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    // UPDATE
    private var accessToken: String = "ACCESS_TOKEN"

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

        // get access token from FlipFlop Lite server
    }

    private fun showStreaming() {
        val intent = Intent(requireActivity(), StreamingActivity::class.java).apply {
            putExtra(PreferenceManager.KEY_ACCESS_TOKEN, accessToken)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showVideoList() {
        findNavController().navigate(R.id.videoList, Bundle().apply {
            putString(PreferenceManager.KEY_ACCESS_TOKEN, accessToken)
        })
    }
}
