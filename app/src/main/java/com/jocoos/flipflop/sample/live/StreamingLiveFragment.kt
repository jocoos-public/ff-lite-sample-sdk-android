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
import com.jocoos.flipflop.FFMessageType
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingLiveFragmentBinding
import com.jocoos.flipflop.sample.utils.launchAndRepeatOnLifecycle
import com.jocoos.flipflop.sample.utils.setMarginBottom
import com.jocoos.flipflop.sample.utils.setMarginTop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StreamingLiveFragment : Fragment() {
    private var _binding: StreamingLiveFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StreamingViewModel by viewModels({requireParentFragment().requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamingLiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.title.setMarginTop(insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
            binding.imageClose.setMarginTop(insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
            binding.backgroundMask.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        launchAndRepeatOnLifecycle(Lifecycle.State.CREATED) {
            launch {
                observeState()
            }
        }

        binding.imageClose.setOnClickListener {
            viewModel.endBroadcast()
        }
        binding.imageEtc.setOnClickListener {
            it.findNavController().navigate(R.id.moreOption)
        }
        binding.imageCamera.setOnClickListener {
            it.findNavController().navigate(R.id.cameraOption)
        }
        binding.messageSend.setOnClickListener {
            showMessageDialog()
        }

        viewModel.requestConfig()
    }

    private suspend fun observeState() {
        viewModel.streamLiveAction.collectLatest {
            when (it) {
                is StreamingLiveState.UpdateCountState -> {
                    binding.imageView.text = it.viewCount.toString()
                }
            }
        }
    }

    // dialog

    private fun showMessageDialog(message: String = "") {
        val dialog = StreamingMessageDialogFragment(
            message = message,
            showEffect = true,
            sendListener = {
                viewModel.sendMessage(FFMessageType.MESSAGE, it, "manager")
            },
            effectListener = { msg ->
                showEffectDialog(msg)
            },
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun showEffectDialog(message: String) {
        val dialog = StreamingChatEffectDialogFragment(
            message = message,
            listener = {
                showMessageDialog(message)
            },
            effectListener = {
                viewModel.sendChatEffect(it)
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }
}
