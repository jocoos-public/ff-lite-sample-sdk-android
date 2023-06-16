package com.jocoos.flipflop.sample.vod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.*
import com.jocoos.flipflop.api.model.Video
import com.jocoos.flipflop.sample.databinding.PlayerVodFragmentBinding
import com.jocoos.flipflop.sample.live.ChatListAdapter

/**
 * check createPlayer()
 */
class PlayerVodFragment : Fragment(), FFLVodPlayerListener {

    private var _binding: PlayerVodFragmentBinding? = null
    private val binding get() = _binding!!
    private val chatListAdapter = ChatListAdapter()

    private var player: FFLVodPlayer? = null
    private lateinit var video: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerVodFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initChatList()
        createPlayer()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        player?.start("VOD_URL")
        binding.playerView.hideController()
    }

    override fun onPause() {
        super.onPause()
        player?.apply {
            stop()
        }
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

    private fun createPlayer() {
        player = FlipFlopLite.getVodPlayer(
            "APP_ID",
            "CHANNEL_KEY",
            "CHAT_TOKEN",
            0, // change it to start time of the live session
            "USER_ID"
        ).apply {
            listener = this@PlayerVodFragment
            enter()
            prepare(requireContext(), binding.playerView)
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

    private fun addJoinMessage(username: String) {
        // show join message
    }

    override fun onPositionChanged(oldPositionMs: Long, newPositionMs: Long) {
        println("onPositionChanged")
    }

    override fun onPrepared() {
        println("onPrepared")
        player?.start("VOD_URL")
    }

    override fun onStarted() {
        println("onStarted")
    }

    override fun onBuffering() {

    }

    override fun onStopped() {
        println("onStopped")
    }

    override fun onCompleted() {
        println("onCompleted")
    }

    override fun onChatMessageReceived(item: FFMessage) {
        when (item.messageType) {
            FFMessageType.JOIN -> {
                addJoinMessage(item.username)
            }
            FFMessageType.MESSAGE -> {
                // do something
            }
            FFMessageType.DM -> {
                // do something
            }
            FFMessageType.COMMAND -> {
                // do something
            }
            else -> {
                // ignore at the moment
            }
        }
    }

    override fun onError(error: FlipFlopException) {
        println("onError : ${error.code} / ${error.message}")
    }
}