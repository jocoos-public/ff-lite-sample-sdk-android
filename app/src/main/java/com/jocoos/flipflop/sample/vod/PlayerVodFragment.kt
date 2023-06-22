package com.jocoos.flipflop.sample.vod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.*
import com.jocoos.flipflop.sample.databinding.PlayerVodFragmentBinding
import com.jocoos.flipflop.sample.live.ChatListAdapter
import com.jocoos.flipflop.sample.live.VodInfo
import com.jocoos.flipflop.sample.utils.PreferenceManager
import com.jocoos.flipflop.sample.utils.toDateTime

/**
 * check createPlayer()
 */
class PlayerVodFragment : Fragment(), FFLVodPlayerListener {

    private var _binding: PlayerVodFragmentBinding? = null
    private val binding get() = _binding!!
    private val chatListAdapter = ChatListAdapter()

    private var player: FFLVodPlayer? = null
    private var vodInfo: VodInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerVodFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().run {
            vodInfo = getParcelable(PreferenceManager.KEY_VOD_INFO)
        }
        if (vodInfo == null) {
            Toast.makeText(requireContext(), "need vod info", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        initChatList()
        createPlayer()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        player?.start(vodInfo!!.vodUrl)
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
            vodInfo!!.chatAppId,
            vodInfo!!.channelKey,
            vodInfo!!.chatToken,
            vodInfo?.liveStartedAt?.toDateTime()?.time ?: 0, // change it to start time of the live session
            vodInfo!!.userId
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
        player?.start(vodInfo!!.vodUrl)
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
        Toast.makeText(requireContext(), "vod has been finished", Toast.LENGTH_SHORT).show()
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