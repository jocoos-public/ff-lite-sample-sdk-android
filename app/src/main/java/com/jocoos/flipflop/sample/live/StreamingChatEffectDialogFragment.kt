package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingChatEffectDialogFragmentBinding

class StreamingChatEffectDialogFragment(
    private val message: String,
    private val listener: ((message: String) -> Unit)? = null,
    private val effectListener: ((chatEffect: ChatEffect) -> Unit)? = null,
) : BottomSheetDialogFragment() {
    private var _binding: StreamingChatEffectDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatEffectListAdapter: ChatEffectListAdapter
    private var selectedChatEffect: ChatEffect? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = StreamingChatEffectDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.messageFrame.setOnClickListener {
            listener?.invoke(message)
            dismiss()
        }
        binding.imageClose.setOnClickListener {
            updateSelectedEffect()
            showTopRegion(false)
        }
        binding.iconEffect.setOnClickListener {
            listener?.invoke(message)
            dismiss()
        }
        binding.imageSend.setOnClickListener {
            selectedChatEffect?.let {
                effectListener?.invoke(it)
            }
            showTopRegion(false)
        }

        binding.chatMessage.text = message

        initChatEffectList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.effectList.adapter = null
    }

    private fun initChatEffectList() {
        val effectList = listOf(
            ChatEffect(R.raw.animated_gif),
            ChatEffect(R.raw.loading_icon_animated_gif),
        )
        chatEffectListAdapter = ChatEffectListAdapter(effectList) { _, position ->
            updateSelectedEffect(effectList[position])
        }
        binding.effectList.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = chatEffectListAdapter
        }
    }

    private fun updateSelectedEffect(chatEffect: ChatEffect? = null) {
        selectedChatEffect = chatEffect

        if (chatEffect != null) {
            Glide.with(binding.root)
                .load(chatEffect.effectResId)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.imageEffect)
        }

        showTopRegion(chatEffect != null)
    }

    private fun showTopRegion(doShow: Boolean) {
        binding.imageEffect.isVisible = doShow
        binding.imageClose.isVisible = doShow
    }
}
