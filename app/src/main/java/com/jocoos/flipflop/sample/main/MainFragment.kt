package com.jocoos.flipflop.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.api.Token
import com.jocoos.flipflop.sample.databinding.MainFragmentBinding
import com.jocoos.flipflop.sample.live.StreamingActivity
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            getAccessToken {
                FlipFlopSampleApp.accessToken = it.accessToken
                showStreaming(it)
            }
        }
        binding.streamingView.setOnClickListener {
            getAccessToken {
                FlipFlopSampleApp.accessToken = it.accessToken
                showVideoList(it)
            }
        }

        // get access token from FlipFlop Lite server
    }

    private fun getAccessToken(onSuccess: (Token) -> Unit) {
        lifecycleScope.launch {
            FlipFlopSampleApp.demoService.accessToken(FlipFlopSampleApp.preferenceManager.userId, FlipFlopSampleApp.preferenceManager.username)
                .onSuccess {
                    onSuccess(it)
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun showStreaming(token: Token) {
        val intent = Intent(requireActivity(), StreamingActivity::class.java).apply {
            putExtra(PreferenceManager.KEY_ACCESS_TOKEN, token.accessToken)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showVideoList(token: Token) {
        findNavController().navigate(R.id.videoList, Bundle().apply {
            putString(PreferenceManager.KEY_ACCESS_TOKEN, token.accessToken)
        })
    }
}
