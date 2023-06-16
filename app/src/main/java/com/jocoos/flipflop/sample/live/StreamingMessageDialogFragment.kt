package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jocoos.flipflop.sample.databinding.StreamingMessageDialogFragmentBinding
import com.jocoos.flipflop.sample.utils.onTextChanged

class StreamingMessageDialogFragment(
    private val message: String,
    private val showEffect: Boolean = false,
    private val sendListener: ((message: String) -> Unit)? = null, // send chat message
    private val effectListener: ((message: String) -> Unit)? = null, // change dialog to chat effect dialog
) : BottomSheetDialogFragment() {
    private var _binding: StreamingMessageDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = StreamingMessageDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageEmoticon.isVisible = showEffect

        binding.imageEmoticon.setOnClickListener {
            val msg = binding.chatEdit.text.toString()
            effectListener?.invoke(msg)

            dismiss()
        }
        binding.chatEdit.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.chatEdit.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.chatEdit.onTextChanged { string, _, _, _ ->
            binding.imageSend.isVisible = string.isNotBlank()
        }
        if (message.isNotBlank()) {
            binding.chatEdit.text.append(message)
        }

        binding.imageSend.setOnClickListener {
            val msg = binding.chatEdit.text.toString().trim()
            if (msg.isNotBlank()) {
                sendListener?.invoke(msg)
                binding.chatEdit.text.clear()
            }
        }
    }
}