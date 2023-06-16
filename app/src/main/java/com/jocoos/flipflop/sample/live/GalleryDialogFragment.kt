package com.jocoos.flipflop.sample.live

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jocoos.flipflop.sample.databinding.ImageListDialogFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryDialogFragment(
    private val uri: Uri,
    private val sendListener: ((Uri) -> Unit)? = null,
    private val dismissListener: ((isChecked: Boolean) -> Unit)? = null,
) : BottomSheetDialogFragment() {
    private var _binding: ImageListDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var pipFileListAdapter: GalleryAdapter

    private var selectedFile: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = ImageListDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.send.setOnClickListener {
            dismiss()
            selectedFile?.let {
                sendListener?.invoke(it)
            }
        }

        initPhotoList()

        lifecycleScope.launch {
            viewModel.loadPhotos(requireContext().contentResolver, uri).collectLatest {
                pipFileListAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.fileList.adapter = null
    }

    private fun initPhotoList() {
        pipFileListAdapter = GalleryAdapter(-1, object : GalleryAdapter.OnItemClickListener {
            override fun onFileClicked(uri: Uri, position: Int) {
                //dismiss()
                //listener?.invoke(uri)
            }

            override fun onCheckClicked(uri: Uri, prevPosition: Int, position: Int) {
                selectedFile = uri
                pipFileListAdapter.updateSelectedItem(position)
                updateSendView(prevPosition != position)
            }
        })
        binding.fileList.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = pipFileListAdapter
        }
    }

    private fun updateSendView(canSend: Boolean) {
        binding.send.isEnabled = canSend
    }

    private fun showEmptyView() {
        binding.imageEmpty.isVisible = true
        binding.textEmpty.isVisible = true
        binding.fileList.isVisible = false
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke(false)
    }
}
