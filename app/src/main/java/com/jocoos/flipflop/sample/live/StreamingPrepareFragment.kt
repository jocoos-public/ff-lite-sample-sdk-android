package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingPrepareFragmentBinding
import com.jocoos.flipflop.sample.utils.launchAndRepeatOnLifecycle
import com.jocoos.flipflop.sample.utils.setMarginBottom
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StreamingPrepareFragment : Fragment() {
    private var _binding: StreamingPrepareFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StreamingViewModel by viewModels({requireParentFragment().requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamingPrepareFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.backgroundBottom.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        binding.imageClose.setOnClickListener {
            viewModel.cancel()
        }
        binding.next.setOnClickListener {
            viewModel.start()
            it.findNavController().navigate(R.id.liveFragment)
        }

        binding.imageCamera.setOnClickListener {
            it.findNavController().navigate(R.id.cameraOption)
        }
        binding.imageEtc.setOnClickListener {
            it.findNavController().navigate(R.id.moreOption)
        }
        binding.imageVideos.setOnClickListener {
            it.findNavController().navigate(R.id.videosOption)
        }

        viewModel.requestConfig()

        launchAndRepeatOnLifecycle(Lifecycle.State.CREATED) {
            launch {
                observeStreamState()
            }
        }
    }

    private suspend fun observeStreamState() {
        viewModel.prepareAction.collectLatest {
            when (it) {
                is PrepareState.RestartState -> {
                    viewModel.restart(it.videoRoomId)
                    findNavController().navigate(R.id.liveFragment)
                }
            }
        }
    }
}
