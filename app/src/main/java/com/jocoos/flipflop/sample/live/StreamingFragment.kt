package com.jocoos.flipflop.sample.live

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jocoos.flipflop.*
import com.jocoos.flipflop.FFLite
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingFragmentBinding
import com.jocoos.flipflop.sample.utils.launchAndRepeatOnLifecycle
import com.jocoos.flipflop.sample.utils.setMarginBottom
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * check createStreamer(), FLStreamerListener
 */
class StreamingFragment : Fragment(), FFLStreamerListener {
    private var _binding: StreamingFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var frontNavController: NavController
    private val viewModel: StreamingViewModel by viewModels()
    private var fflStreamer: FFLStreamer? = null

    private val chatListAdapter = ChatListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = StreamingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        frontNavController = (childFragmentManager.findFragmentById(R.id.nav_host_streaming_front_fragment) as NavHostFragment)
            .navController.apply {
                setGraph(R.navigation.nav_streaming_front)
            }

        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { _, insets ->
            binding.liveView.setMarginBottom(insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)

            insets
        }

        launchAndRepeatOnLifecycle(Lifecycle.State.CREATED) {
            launch {
                observeStreamState()
            }
        }

        createStreamer()
        initChatList()
    }

    override fun onResume() {
        super.onResume()
        fflStreamer?.retry()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fflStreamer?.exit()

        binding.chatList.adapter = null
        _binding = null
    }

    private suspend fun observeStreamState() {
        viewModel.streamAction.collectLatest {
            when (it) {
                is StreamingState.CameraActionState -> {
                    processCameraAction(it.cameraAction)
                }
                is StreamingState.FilterState -> {
                    fflStreamer?.liveManager()?.setFilter(it.filterType)
                }
                is StreamingState.ExposureState -> {
                    fflStreamer?.liveManager()?.setExposureCompensation(it.value)
                }
                is StreamingState.MainState -> {
                    processMainAction(it.mainAction)
                }
                is StreamingState.TransitionState -> {
                    Glide.with(this)
                        .asBitmap()
                        .load(it.imageUrl)
                        .skipMemoryCache(true)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val transitionParams = FFTransitionParams(
                                    transitionType = it.type,
                                    duration = 1000,
                                )
                                fflStreamer?.liveManager()?.showImage(resource, FFScaleMode.CENTER_FIT, transitionParams)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
                is StreamingState.NormalState -> {
                    fflStreamer?.liveManager()?.hideImage()
                }
                is StreamingState.StartLiveState -> {
                    // start live streaming
                    fflStreamer?.start()
                    binding.playTime.start(System.currentTimeMillis())
                }
                is StreamingState.MessageSendState -> {
                    if (it.receiver == null) {
                        fflStreamer?.liveChat()?.sendMessage(it.message, it.customType, it.data)
                    } else {
                        fflStreamer?.liveChat()?.sendDirectMessage(it.receiver, it.message, it.customType, it.data)
                    }
                }
                is StreamingState.EffectState -> {
                    fflStreamer?.liveManager()?.setOverlayImage(resources.openRawResource(it.chatEffect.effectResId), FFScaleMode.NONE)
                    viewModel.endChatEffect()
                }
                else -> {

                }
            }
        }
    }

    /**
     * initialize streamer for live streaming
     *
     * you should get parameters(appId, user info, streamKey, chatToken, channelKey) from server
     * (client app <--> your server <--> flipflop lite server)
     */
    private fun createStreamer() {
        fflStreamer = FlipFlopLite.getStreamer(
            "APP_ID",
            FFLite.User("USER_ID", "USERNAME"),
            "STREAM_KEY",
            "CHAT_TOKEN",
            "CHANNEL_KEY"
        ).apply {
            listener = this@StreamingFragment
            // comment this out if you do not want to test chatting
            enter()
            prepare(requireContext(), binding.liveView, FFStreamerConfig(videoBitrate = 3000 * 1024, fps = 30, sampleRate = 44100))
            zoomChangeListener = object : FFZoomChangeListener {
                override fun onZoomChanged(zoomFactor: Float) {
                    binding.zoomFactor.text = zoomFactor.toString()
                }
            }
        }

        // need some time to get exposure after initializing streamer
        // that's why this uses delay
        viewModel.requestInitExposure(300)
    }

    private fun initCameraExposure() {
        fflStreamer?.liveManager()?.let {
            viewModel.setExposureRange(it.getExposureCompensationRange())
        }
    }

    private fun initChatList() {
        with(binding.chatList) {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = chatListAdapter
        }
    }

    private fun addJoinMessage(username: String) {
        // show join message
    }

    private fun finish() {
        fflStreamer?.listener = null
    }

    private fun processMainAction(action: MainAction) {
        when (action) {
            MainAction.CANCEL -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            MainAction.INIT_EXPOSURE -> {
                initCameraExposure()
            }
            MainAction.END_CHAT_EFFECT -> {
                fflStreamer?.liveManager()?.setOverlayImage()
            }
        }
    }

    private fun processCameraAction(action: CameraAction) {
        when (action) {
            CameraAction.ZOOM_1X -> {
                fflStreamer?.liveManager()?.zoom(1f)
            }
            CameraAction.ZOOM_2X -> {
                fflStreamer?.liveManager()?.zoom(2f)
            }
            CameraAction.CHANGE_MIRROR -> {
                fflStreamer?.liveManager()?.changeMirrorMode()
            }
            CameraAction.SWITCH_CAMERA -> {
                fflStreamer?.liveManager()?.switchCamera()
            }
        }
    }

    // FFStreamerListener
    override fun onPrepared() {
        println("onPrepared")
    }

    override fun onStarted() {
        println("onStarted")
    }

    override fun onStopped() {
        println("onStopped")
    }

    override fun onChatMessageReceived(item: FFMessage) {
        when (item.messageType) {
            FFMessageType.JOIN -> {
                addJoinMessage(item.username)
                viewModel.updateLiveCount(item.totalWatchCount)
            }
            FFMessageType.LEAVE -> {
                viewModel.updateLiveCount(item.totalWatchCount)
            }
            FFMessageType.MESSAGE -> {
                when (item.customType) {
                    "manager" -> {
                        chatListAdapter.add(ChatItem(item.userId, item.username, item.message, item.messageId ?: "0", item.channelKey))
                    }
                    else -> {
                        val message = if (item.meta?.hidden == true) {
                            "hidden message"
                        } else {
                            item.message
                        }
                        chatListAdapter.add(ChatItem(item.userId, item.username, message, item.messageId ?: "0", item.channelKey))
                    }
                }
            }
            FFMessageType.DM -> {
                // do something
            }
            FFMessageType.ADMIN -> {
                // do something
            }
            else -> {
                // ignore at the moment
            }
        }
    }

    override fun onVideoBitrateChanged(newBitrate: Int) {

    }

    override fun onInSufficientBW() {
        println("onInSufficientBW")
    }

    override fun onSufficientBW() {
        println("onSufficientBW")
    }

    override fun onError(error: FlipFlopException) {
        println("onError - ${error.code} / ${error.message}")
    }
}