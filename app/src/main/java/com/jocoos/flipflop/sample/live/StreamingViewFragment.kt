package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.*
import com.jocoos.flipflop.sample.databinding.StreamingViewFragmentBinding
import com.jocoos.flipflop.sample.utils.PreferenceManager

/**
 * check createPlayer()
 */
class StreamingViewFragment : Fragment(), FFLLivePlayerListener {
    companion object {
        fun newInstance() = StreamingViewFragment()
    }

    private var _binding: StreamingViewFragmentBinding? = null
    private val binding get() = _binding!!

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

        createPlayer()
        initChatList()
    }

    override fun onStart() {
        super.onStart()
    }

    /**
     * VIDEO_URL : you should get it from server
     */
    override fun onResume() {
        super.onResume()
        player?.apply {
            enter()
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
    private fun createPlayer() {
        binding.liveView.visibility = VISIBLE

        player = FlipFlopLite.getLivePlayer(
            liveWatchInfo!!.chatAppId,
            FFLite.User(liveWatchInfo!!.userId, liveWatchInfo!!.userName),
            liveWatchInfo!!.chatToken,
            liveWatchInfo!!.channelKey
        ).apply {
            listener = this@StreamingViewFragment
            enter()
            prepare(requireContext(), binding.liveView)
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

    private fun showMessageDialog(message: String = "") {
        val dialog = StreamingMessageDialogFragment(
            message = message,
            sendListener = {
                player?.liveChat()?.sendMessage(it)
            },
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    override fun onPrepared() {
        println("onPrepared")
    }

    override fun onStarted() {
        println("onStarted")
    }

    override fun onBuffering() {
        println("onBuffering")
    }

    override fun onCompleted() {
        println("onCompleted")
        Toast.makeText(requireContext(), "live has been finished", Toast.LENGTH_SHORT).show()
    }

    override fun onStopped() {
        println("onStopped")
    }

    override fun onChatMessageReceived(item: FFMessage) {
        when (item.messageType) {
            FFMessageType.JOIN -> {
                chatListAdapter.add(ChatItem(item.userId, item.username, "joined ${item.username}", item.messageId ?: "0", item.channelKey))
            }
            FFMessageType.LEAVE -> {
                // do something
            }
            FFMessageType.MESSAGE -> {
                // do something
                chatListAdapter.add(ChatItem(item.userId, item.username, item.message, item.messageId ?: "0", item.channelKey))
            }
            FFMessageType.DM -> {
                // do something
            }
            FFMessageType.ADMIN -> {
                when (item.customType) {
                    COMMAND_MESSAGE_DELETE -> {
                        // do something
                    }
                    COMMAND_MESSAGE_DELETE_BY_ADMIN -> {
                        // do something
                    }
                    COMMAND_MSG -> {
                        // do something
                    }
                    COMMAND_DM -> {
                        // do something
                    }
                    COMMAND_BLOCK -> {
                        // do something
                    }
                    COMMAND_ALERT -> {
                        // do something
                    }
                    COMMAND_PROFANITY_REPLACE -> {
                        // do something
                    }
                    COMMAND_PRIVATE_MESSAGE -> {
                        // do something
                    }
                    COMMAND_PUBLIC_MESSAGE -> {
                        // do something
                    }
                    else -> {
                        // ignore
                    }
                }
            }
            else -> {
                // ignore at the moment
            }
        }
    }

    override fun onError(error: FlipFlopException) {

    }
}