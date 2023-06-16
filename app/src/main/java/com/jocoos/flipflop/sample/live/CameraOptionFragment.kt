package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.ExposureCompensationRange
import com.jocoos.flipflop.FFFilterType
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.CameraOptionFragmentBinding
import com.jocoos.flipflop.sample.utils.setMarginBottom

/**
 * camera options
 * : zoom, front or back switch, mirror, exposure, filter
 */
class CameraOptionFragment : Fragment() {
    private var _binding: CameraOptionFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StreamingViewModel by viewModels({requireParentFragment().requireParentFragment()})

    private var selectedOptionView: View? = null

    private lateinit var filterAdapter: FilterAdapter

    private var filterList: List<FilterInfo> = listOf(
        FilterInfo(FFFilterType.NO_FILTER, "original", R.raw.basic),
        FilterInfo(FFFilterType.TONE_WARM, "warm", R.raw.warm),
        FilterInfo(FFFilterType.TONE_DARK, "dark", R.raw.dark),
        FilterInfo(FFFilterType.TONE_VIVID_WARM, "vivid", R.raw.vivid_warm),
        FilterInfo(FFFilterType.TONE_VIVID_DARK, "vivid dark", R.raw.vivid_dark),
        FilterInfo(FFFilterType.TONE_DRAMATIC_COOL, "cool", R.raw.cool),
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = CameraOptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.backgroundBottom.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        initFilterList()
        changeOption(null, binding.imageSize)

        binding.imageBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.confirm.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.imageSize.setOnClickListener {
            changeOption(selectedOptionView, it)

            showZoomDetailViews(true)
            showExposureViews(false)
            showFilterDetailViews(false)
        }
        binding.imageReversed.setOnClickListener {
            viewModel.changeMirrorMode()
            changeOption(selectedOptionView, it)

            hideDetailViews()
        }
        binding.imageRotate.setOnClickListener {
            viewModel.switchCamera()
            changeOption(selectedOptionView, it)

            hideDetailViews()
        }
        binding.imageExposure.setOnClickListener {
            changeOption(selectedOptionView, it)

            showZoomDetailViews(false)
            showExposureViews(true)
            showFilterDetailViews(false)
        }
        binding.imageFilter.setOnClickListener {
            changeOption(selectedOptionView, it)

            showZoomDetailViews(false)
            showExposureViews(false)
            showFilterDetailViews(true)
        }

        binding.imageZoom1x.setOnClickListener {
            viewModel.zoom1X()
            changeZoom(CameraZoom.ZOOM_1X)
        }
        binding.imageZoom2x.setOnClickListener {
            viewModel.zoom2X()
            changeZoom(CameraZoom.ZOOM_2X)
        }

        viewModel.streamingOption.exposureRange?.let { range ->
            initExposureView(range)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.filterList.adapter = null
        selectedOptionView = null
    }

    private fun initExposureView(exposureRange: ExposureCompensationRange) {
        binding.cameraExposure.value = 0f
        binding.cameraExposure.valueFrom = exposureRange.min
        binding.cameraExposure.valueTo = exposureRange.max
        binding.cameraExposure.stepSize = exposureRange.step

        binding.cameraExposure.addOnChangeListener { _, value, _ ->
            binding.exposureValue.text = if (value > 0) {
                getString(R.string.exposure_max_value, value.toString())
            } else {
                value.toString()
            }
            viewModel.setExposureValue(value)
        }

        binding.cameraExposure.isVisible = true
    }

    private fun initFilterList() {
        filterAdapter = FilterAdapter(filterList) { _, position ->
            viewModel.setFilter(filterList[position].filterType)
            filterAdapter.selectedPosition(position)
        }
        binding.filterList.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            adapter = filterAdapter
        }
    }

    private fun changeOption(prevSelectedOptionView: View?, selectedOptionView: View) {
        this.selectedOptionView = selectedOptionView

        prevSelectedOptionView?.setBackgroundResource(0)
        selectedOptionView.setBackgroundResource(R.drawable.circle_color_primary)
    }

    private fun changeZoom(cameraZoom: CameraZoom) {
        when (cameraZoom) {
            CameraZoom.ZOOM_1X -> {
                binding.imageZoom1x.setBackgroundResource(R.drawable.circle_border_white)
                binding.imageZoom2x.setBackgroundResource(R.drawable.circle_border_white20)
            }
            CameraZoom.ZOOM_2X -> {
                binding.imageZoom1x.setBackgroundResource(R.drawable.circle_border_white20)
                binding.imageZoom2x.setBackgroundResource(R.drawable.circle_border_white)
            }
        }
    }

    private fun hideDetailViews() {
        showZoomDetailViews(false)
        showExposureViews(false)
        showFilterDetailViews(false)
    }

    private fun showZoomDetailViews(isVisible: Boolean) {
        binding.imageZoom1x.isVisible = isVisible
        binding.imageZoom2x.isVisible = isVisible
    }

    private fun showExposureViews(isVisible: Boolean) {
        binding.exposures.isVisible = isVisible
    }

    private fun showFilterDetailViews(isVisible: Boolean) {
        binding.filterList.isVisible = isVisible
    }
}
