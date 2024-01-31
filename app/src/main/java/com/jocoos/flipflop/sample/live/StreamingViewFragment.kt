package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.FFLErrorCode
import com.jocoos.flipflop.FFLLivePlayer
import com.jocoos.flipflop.FFLLivePlayerOptions
import com.jocoos.flipflop.FlipFlopException
import com.jocoos.flipflop.FlipFlopLite
import com.jocoos.flipflop.api.model.FFLMessage
import com.jocoos.flipflop.api.model.Origin
import com.jocoos.flipflop.events.BroadcastState
import com.jocoos.flipflop.events.PlayerEvent
import com.jocoos.flipflop.events.PlayerState
import com.jocoos.flipflop.events.collect
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingViewFragmentBinding
import com.jocoos.flipflop.sample.utils.DialogBuilder
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.launch

class StreamingViewFragment : Fragment() {
    companion object {
        fun newInstance() = StreamingViewFragment()
    }

    private var _binding: StreamingViewFragmentBinding? = null
    private val binding get() = _binding!!

    private var accessToken: String = ""
    private var liveWatchInfo: LiveWatchInfo? = null
    private var player: FFLLivePlayer? = null
    private val chatListAdapter = ChatListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamingViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().run {
            accessToken = getString(PreferenceManager.KEY_ACCESS_TOKEN) ?: ""
            liveWatchInfo = getParcelable(PreferenceManager.KEY_LIVE_WATCH_INFO)
        }
        if (liveWatchInfo == null) {
            Toast.makeText(requireContext(), "need live info", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        binding.messageSend.setOnClickListener {
            showMessageDialog("")
        }

        createPlayer(accessToken, liveWatchInfo!!.videoRoomId, liveWatchInfo!!.channelId)
        initChatList()

        lifecycleScope.launch {
            player?.livePlayerEvent?.collect { event ->
                when (event) {
                    is PlayerEvent.PlayerStateChanged -> {
                        chatListAdapter.add(ChatItem("appUserId", "appUsername", event.state.name, "0"))
                        when (event.state) {
                            PlayerState.PREPARED -> {

                            }
                            PlayerState.STARTED -> {

                            }
                            PlayerState.BUFFERING -> {

                            }
                            PlayerState.STOPPED -> {

                            }
                            PlayerState.COMPLETED -> {

                            }
                            PlayerState.CLOSED -> {
                                showCloseDialog(
                                    title = getString(R.string.finish_live),
                                    content = "Thank you!",
                                    confirmHandler = {
                                        findNavController().navigateUp()
                                    }
                                )
                            }
                        }
                    }
                    is PlayerEvent.BroadcastStateChanged -> {
                        chatListAdapter.add(ChatItem("appUserId", "appUsername", event.state.name, "0"))
                        when (event.state) {
                            BroadcastState.ACTIVE -> {

                            }
                            BroadcastState.INACTIVE -> {

                            }
                        }
                    }
                    is PlayerEvent.MessageReceived -> {
                        handleMessage(event.message)
                    }
                    is PlayerEvent.PlayerError -> {
                        handleError(event.code, event.message)
                    }
                }
            }
        }
    }

    /**
     * VIDEO_URL : you should get it from server
     */
    override fun onResume() {
        super.onResume()
        player?.apply {
            start(liveWatchInfo!!.liveUrl)
        }
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.chatList.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.exit()
    }

    /**
     * initialize live player to watch live
     *
     * you should get parameters(appId, user info, gossipToken, channelKey) from server api
     * (client app -> belive server -> flipflop lite server)
     */
    private fun createPlayer(accessToken: String, videoRoomId: Long, channelId: Long) {
        binding.liveView.visibility = VISIBLE

        player = FlipFlopLite.getLivePlayer(accessToken, videoRoomId, channelId).apply {
            prepare(requireContext(), binding.liveView, FFLLivePlayerOptions(false))
            enter()
        }
    }

    private fun initChatList() {
        binding.chatList.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = chatListAdapter
        }
    }

    private fun sendMessage(message: String) {
        lifecycleScope.launch {
            player?.liveChat()?.sendMessage(message)
        }
    }

    private fun showMessageDialog(message: String = "") {
        val dialog = StreamingMessageDialogFragment(
            message = message,
            sendListener = {
                sendMessage(it)
            },
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun showCloseDialog(title: String, content: String, confirmHandler: () -> Unit) {
        DialogBuilder.showShortDialog(requireContext(), title, content,
            confirmListener = {
                confirmHandler.invoke()
            },
            cancelable = false
        )
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
            FFLErrorCode.SERVER_VIDEO_ROOM_JOIN_ERROR -> {
                // failed to join live
            }
            FFLErrorCode.SERVER_VIDEO_ROOM_LEAVE_ERROR -> {
                // failed to leave live
            }
        }
    }
}