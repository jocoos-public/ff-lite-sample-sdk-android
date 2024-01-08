package com.jocoos.flipflop.sample.live

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jocoos.flipflop.*
import com.jocoos.flipflop.api.model.FFLMessage
import com.jocoos.flipflop.api.model.Origin
import com.jocoos.flipflop.events.BroadcastState
import com.jocoos.flipflop.events.StreamerEvent
import com.jocoos.flipflop.events.StreamerState
import com.jocoos.flipflop.events.collect
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingFragmentBinding
import com.jocoos.flipflop.sample.main.VideoInfo
import com.jocoos.flipflop.sample.utils.DialogBuilder
import com.jocoos.flipflop.sample.utils.IOCoroutineScope
import com.jocoos.flipflop.sample.utils.PreferenceManager
import com.jocoos.flipflop.sample.utils.launchAndRepeatOnLifecycle
import com.jocoos.flipflop.sample.utils.setMarginBottom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * check createStreamer(), FLStreamerListener
 */
class StreamingFragment : Fragment() {
    private var _binding: StreamingFragmentBinding? = null
    private val binding get() = _binding!!
    private val scope: CoroutineScope = IOCoroutineScope()

    private var videoRoomId: Long = -1

    private lateinit var frontNavController: NavController
    private val viewModel: StreamingViewModel by viewModels()
    private var accessToken = ""
    private var fflStreamer: FFLStreamer? = null
    private var fflPlayer: FFLLivePlayer? = null

    private val chatListAdapter = ChatListAdapter()
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var finished = false
    private var isStreaming = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this.isEnabled = false
                handleBackButton()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    private fun handleBackButton() {
        showCloseDialog(
            title = getString(R.string.finish_live),
            content = getString(R.string.want_to_finish_live),
            confirmHandler = {
                endLiveStreaming()
            }
        )
    }

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

        requireArguments().run {
            accessToken = getString(PreferenceManager.KEY_ACCESS_TOKEN) ?: ""
        }

        if (accessToken.isEmpty()) {
            Toast.makeText(requireContext(), "access token should not be empty", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }

        createStreamer(accessToken)
        initChatList()

        lifecycleScope.launch {
            fflStreamer?.streamerEvent?.collect { event ->
                when (event) {
                    is StreamerEvent.StreamerStateChanged -> {
                        chatListAdapter.add(ChatItem("USER_ID", "USERNAME", event.state.name, "0"))
                        when (event.state) {
                            StreamerState.PREPARED -> {
                            }
                            StreamerState.STARTED -> {
                                binding.playTime.start(System.currentTimeMillis())
                            }
                            StreamerState.STOPPED -> {
                            }
                            StreamerState.CLOSED -> {
                                findNavController().navigate(R.id.finishFragment)
                            }
                            StreamerState.TERMINATED -> {

                            }
                        }
                    }
                    is StreamerEvent.BroadcastStateChanged -> {
                        when (event.state) {
                            BroadcastState.ACTIVE -> {
                                binding.centerInfo.isVisible = false
                                chatListAdapter.add(ChatItem("USER_ID", "USERNAME", "ACTIVE", "0"))
                            }
                            BroadcastState.INACTIVE -> {
                                chatListAdapter.add(ChatItem("USER_ID", "USERNAME", "INACTIVE", "0"))
                            }
                        }
                    }

                    is StreamerEvent.LiveExists -> {
                        val videoRoom = event.videoRoom
                        showRestartDialog(videoRoom.title, getString(R.string.restart_live_content)) {
                            viewModel.prepareRestart(videoRoom.title, videoRoom.id)
                        }
                    }
                    is StreamerEvent.StreamAlarmPublished -> {
                        chatListAdapter.add(ChatItem("USER_ID", "USERNAME", event.state.name, "0"))
                    }
                    is StreamerEvent.CameraZoomChanged -> {
                        println("zoom: ${event.zoomFactor}")
                    }
                    is StreamerEvent.VideoBitrateChanged -> {
                        println("bitrate: ${event.bitrate}")
                    }
                    is StreamerEvent.MessageReceived -> {
                        handleMessage(event.message)
                    }
                    is StreamerEvent.StreamerError -> {
                        handleError(event.code, event.message)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fflStreamer?.stop()
        fflPlayer?.stop()
    }

    override fun onResume() {
        super.onResume()
        fflStreamer?.retry()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.chatList.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        fflPlayer?.exit()
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
                is StreamingState.BitrateChanger -> {
                    if (it.value == 0) {
                        fflStreamer?.liveManager()?.enableAdaptiveBitrate()
                    } else {
                        fflStreamer?.liveManager()?.setVideoBitrateOnFly(it.value)
                    }
                }
                is StreamingState.NormalState -> {
                    fflStreamer?.liveManager()?.hideImage()
                }
                is StreamingState.StartLiveState -> {
                    // start live streaming
                    startLiveStreaming()
                }
                is StreamingState.RestartLiveState -> {
                    fflStreamer?.restart(it.videoRoomId)
                    isStreaming = true
                }
                is StreamingState.EndLiveState -> {
                    handleBackButton()
                }
                is StreamingState.MessageSendState -> {
                    if (it.receiver == null) {
                        fflStreamer?.liveChat()?.sendMessage(it.message)
                    }
                }
                is StreamingState.EffectState -> {
                    fflStreamer?.liveManager()?.setOverlayImage(resources.openRawResource(it.chatEffect.effectResId), FFScaleMode.NONE)
                    viewModel.endChatEffect()
                }
                is StreamingState.PlayLive -> {
                    createPlayer(accessToken, it.videoInfo)
                }
                else -> {

                }
            }
        }
    }

    /**
     * initialize streamer for live streaming
     */
    private fun createStreamer(accessToken: String) {
        fflStreamer = FlipFlopLite.getStreamer(accessToken).apply {
            prepare(requireContext(), binding.liveView, FFStreamerConfig(videoBitrate = 2500 * 1024, fps = 30, sampleRate = 44100))
            liveManager()?.enableAdaptiveBitrate()
            setVideoRoomInfo("Live by ${FlipFlopSampleApp.preferenceManager.username}")
        }
    }

    private fun createPlayer(accessToken: String, videoInfo: VideoInfo) {
        fflPlayer = FlipFlopLite.getLivePlayer(accessToken, videoInfo.videoRoomId, videoInfo.channelId).apply {
            prepare(requireContext(), binding.livePlayer)
        }.apply {
            start(videoInfo.liveUrl!!)
            enter()
        }
    }

    private fun startLiveStreaming() {
        binding.centerInfo.text = "Preparing live streaming.\nplease wait a minute for this message to disappear!"
        binding.centerInfo.isVisible = true

        fflStreamer?.enter()
        fflStreamer?.start()
        binding.playTime.start(System.currentTimeMillis())

        isStreaming = true
    }

    private fun endLiveStreaming() {
        if (finished) {
            return
        }

        finished = true

        if (!isStreaming) {
            findNavController().navigate(R.id.finishFragment)
        } else {
            fflStreamer?.stop()
            fflStreamer?.exit()

            binding.centerInfo.text = "Ending live streaming.\nplease wait a minute!\n"
            binding.centerInfo.isVisible = true
        }
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

    private fun finish() {

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

    private fun handleMessage(message: FFLMessage) {
        when (message.origin) {
            Origin.APP -> {
                chatListAdapter.add(ChatItem(message.appUserId, message.appUsername, message.message, "0"))
            }
            Origin.MEMBER -> {
                chatListAdapter.add(ChatItem(message.appUserId, message.appUsername, message.message, "0"))
            }
            Origin.SYSTEM -> {
                when (message.customType) {
                    "JOINED" -> {
                        chatListAdapter.add(ChatItem(message.appUserId, message.appUsername, message.customType, "0"))
                    }
                    "LEAVED" -> {
                        chatListAdapter.add(ChatItem(message.appUserId, message.appUsername, "LEFT", "0"))
                    }
                    "CHANNEL_STAT_UPDATED" -> {
                        // show participant count
                    }
                    else -> {

                    }
                }
            }
            else -> {

            }
        }
    }

    private fun handleError(code: Int, message: String) {
        println("error : $code / $message")
        when (code) {
            FFLErrorCode.MEDIA_CONNECTION_FAILED -> {
                // failed to connect to media server
            }
            FFLErrorCode.MEDIA_STREAMING_REQUEST_FAILED -> {
                // failed to start live streaming
            }
            FFLErrorCode.MEDIA_STREAMING_SEND_FAILED -> {
                // failed to send data to media server
            }
        }
    }

    private fun showRestartDialog(title: String, content: String, confirmHandler: () -> Unit) {
        DialogBuilder.showShortDialog(requireContext(), title, content,
            confirmListener = {
                confirmHandler.invoke()
            },
            cancelable = false
        )
    }

    private fun showCloseDialog(title: String, content: String, confirmHandler: () -> Unit) {
        DialogBuilder.showShortDialog(requireContext(), title, content,
            confirmListener = {
                confirmHandler.invoke()
            },
            cancelable = false
        )
    }
}