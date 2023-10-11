package com.jocoos.flipflop.sample.vod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.*
import com.jocoos.flipflop.api.model.FFLMessage
import com.jocoos.flipflop.api.model.Origin
import com.jocoos.flipflop.events.PlayerState
import com.jocoos.flipflop.events.VodPlayerEvent
import com.jocoos.flipflop.events.collect
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.PlayerVodFragmentBinding
import com.jocoos.flipflop.sample.live.ChatItem
import com.jocoos.flipflop.sample.live.ChatListAdapter
import com.jocoos.flipflop.sample.live.VodInfo
import com.jocoos.flipflop.sample.utils.DialogBuilder
import com.jocoos.flipflop.sample.utils.PreferenceManager
import kotlinx.coroutines.launch

class PlayerVodFragment : Fragment() {

    private var _binding: PlayerVodFragmentBinding? = null
    private val binding get() = _binding!!
    private val chatListAdapter = ChatListAdapter()

    private var player: FFLVodPlayer? = null
    private var accessToken: String = ""
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
            accessToken = getString(PreferenceManager.KEY_ACCESS_TOKEN) ?: ""
            vodInfo = getParcelable(PreferenceManager.KEY_VOD_INFO)
        }
        if (vodInfo == null) {
            Toast.makeText(requireContext(), "need vod info", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        initChatList()
        createPlayer(accessToken, vodInfo!!.channelId, vodInfo!!.liveStartedAt)

        lifecycleScope.launch {
            player?.vodPlayerEvent?.collect { event ->
                when (event) {
                    is VodPlayerEvent.PlayerStateChanged -> {
                        chatListAdapter.add(ChatItem("appUserId", "appUsername", event.state.name, "0"))
                        when (event.state) {
                            PlayerState.PREPARED -> {
                                lifecycleScope.launch {
                                    startPlayer()
                                }
                            }
                            PlayerState.STARTED -> {
                            }
                            PlayerState.BUFFERING -> {

                            }
                            PlayerState.STOPPED -> {

                            }
                            PlayerState.CLOSED -> {

                            }
                            PlayerState.COMPLETED -> {
                                showCompleteDialog()
                            }
                        }
                    }
                    is VodPlayerEvent.PositionChanged -> {
                        handlePositionChanged(event.oldPositionMs, event.newPositionMs)
                    }
                    is VodPlayerEvent.MessageReceived -> {
                        handleMessage(event.message)
                    }
                    is VodPlayerEvent.PlayerError -> {
                        handleError(event.code, event.message)
                    }
                }
            }
        }

        player?.apply {
            prepare(requireContext(), binding.playerView)
            enter()
        }
    }

    override fun onResume() {
        super.onResume()
        startPlayer()
        binding.playerView.hideController()
    }

    override fun onPause() {
        super.onPause()
        stopPlayer()
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

    private fun createPlayer(accessToken: String, channelId: Long, liveStartedAt: String) {
        player = FlipFlopLite.getVodPlayer(accessToken, channelId, liveStartedAt).apply {
            prepare(requireContext(), binding.playerView)
            enter()
        }
    }

    private fun startPlayer() {
        vodInfo?.vodUrl?.let {
            player?.start(it)
        }
    }

    private fun stopPlayer() {
        player?.stop()
    }

    private fun initChatList() {
        binding.chatList.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = chatListAdapter
        }
    }

    private fun showCompleteDialog() {
        DialogBuilder.showShortDialog(
            context = requireContext(),
            title = getString(R.string.finish),
            content = getString(R.string.want_to_finish_vod),
            dismissListener = {
                findNavController().navigateUp()
            },
            cancelable = false
        )
    }

    private fun handlePositionChanged(oldPositionMs: Long, newPositionMs: Long) {
        chatListAdapter.removeAll()
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
        println("error: $code / $message")
    }
}