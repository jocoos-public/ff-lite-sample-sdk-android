package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.jocoos.flipflop.FFTransitionType
import com.jocoos.flipflop.sample.databinding.MoreOptionFragmentBinding
import com.jocoos.flipflop.sample.utils.setMarginBottom

/**
 * - show or hide image with transition
 * - mic on / off
 */
class MoreOptionFragment : Fragment() {
    private var _binding: MoreOptionFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StreamingViewModel by viewModels({requireParentFragment().requireParentFragment()})

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = MoreOptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.backgroundBottom.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        binding.imageBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.confirm.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.micOn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMicOn(isChecked)
        }
        binding.imageTransitionNormal.setOnClickListener {
            viewModel.setNormalMode()
        }
        binding.imagePip.setOnClickListener {
            showImageListDialog(FFTransitionType.FADE_IN_PIP)
        }
        binding.imageTransitionFade.setOnClickListener {
            showImageListDialog(FFTransitionType.FADE_IN_OUT)
        }
        binding.imageTransitionTop.setOnClickListener {
            showImageListDialog(FFTransitionType.SLIDE_TO_TOP)
        }
        binding.imageTransitionBottom.setOnClickListener {
            showImageListDialog(FFTransitionType.SLIDE_TO_CAMERA_BOTTOM)
        }
        binding.imageTransitionLeft.setOnClickListener {
            showImageListDialog(FFTransitionType.SLIDE_TO_LEFT)
        }
    }

    private fun showImageListDialog(transitionType: FFTransitionType) {
        val dialog = GalleryDialogFragment(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            sendListener = {
                viewModel.setTransitionWithImage(it, transitionType)
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }
}
