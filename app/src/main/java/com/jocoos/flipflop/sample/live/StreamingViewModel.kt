package com.jocoos.flipflop.sample.live

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jocoos.flipflop.ExposureCompensationRange
import com.jocoos.flipflop.FFFilterType
import com.jocoos.flipflop.FFMessageType
import com.jocoos.flipflop.FFTransitionType
import com.jocoos.flipflop.sample.utils.onLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class MainAction { CANCEL, INIT_EXPOSURE, END_CHAT_EFFECT }

enum class CameraAction { ZOOM_1X, ZOOM_2X, CHANGE_MIRROR, SWITCH_CAMERA }

sealed class StreamingState {
    data class MainState(val mainAction: MainAction) : StreamingState()
    data class CameraActionState(val cameraAction: CameraAction) : StreamingState()
    data class FilterState(val filterType: FFFilterType) : StreamingState()
    data class TransitionState(val imageUrl: Uri, val type: FFTransitionType) : StreamingState()
    data class ExposureState(val value: Float) : StreamingState()
    data class MicOnState(val isChecked: Boolean) : StreamingState()
    data class EffectState(val chatEffect: ChatEffect) : StreamingState()
    data class MessageSendState(val messageType: FFMessageType, val message: String, val customType: String, val data: String, val receiver: String? = null) : StreamingState()
    object NormalState : StreamingState()
    object StartLiveState : StreamingState()
}

class StreamingOption {
    var exposureRange: ExposureCompensationRange? = null
    var exposureValue: Float = 0f
}

sealed class StreamingOptionState {
    data class OptionState(val streamingOption: StreamingOption) : StreamingOptionState()
}

sealed class StreamingLiveState {
    data class UpdateCountState(val viewCount: Long) : StreamingLiveState()
}

enum class CameraZoom { ZOOM_1X, ZOOM_2X }

class StreamingViewModel : ViewModel() {
    private val _streamAction: MutableSharedFlow<StreamingState> by lazy {
        MutableSharedFlow()
    }
    val streamAction = _streamAction.asSharedFlow()

    private val _streamConfigAction: MutableSharedFlow<StreamingOptionState> by lazy {
        MutableSharedFlow()
    }
    val streamConfigAction = _streamConfigAction.asSharedFlow()

    private val _streamLiveAction: MutableSharedFlow<StreamingLiveState> by lazy {
        MutableSharedFlow()
    }
    val streamLiveAction = _streamLiveAction.asSharedFlow()

    val streamingOption = StreamingOption()

    fun changeMirrorMode() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.CameraActionState(CameraAction.CHANGE_MIRROR))
        }
    }

    fun switchCamera() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.CameraActionState(CameraAction.SWITCH_CAMERA))
        }
    }

    fun zoom1X() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.CameraActionState(CameraAction.ZOOM_1X))
        }
    }

    fun zoom2X() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.CameraActionState(CameraAction.ZOOM_2X))
        }
    }

    fun setExposureValue(exposureValue: Float) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.ExposureState(exposureValue))
        }
        streamingOption.exposureValue = exposureValue
    }

    fun setFilter(filterType: FFFilterType) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.FilterState(filterType))
        }
    }

    fun requestConfig() {
        onLaunch(Dispatchers.Main) {
            _streamConfigAction.emit(StreamingOptionState.OptionState(streamingOption))
        }
    }

    fun cancel() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.MainState(MainAction.CANCEL))
        }
    }

    fun requestInitExposure(delay: Long = 0) {
        onLaunch(Dispatchers.Main) {
            delay(delay)
            _streamAction.emit(StreamingState.MainState(MainAction.INIT_EXPOSURE))
        }
    }

    fun setExposureRange(exposureRange: ExposureCompensationRange? = null) {
        streamingOption.exposureRange = exposureRange
    }

    fun setTransitionWithImage(imageUrl: Uri, transitionType: FFTransitionType) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.TransitionState(imageUrl, transitionType))
        }
    }

    fun setNormalMode() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.NormalState)
        }
    }

    fun setMicOn(isChecked: Boolean) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.MicOnState(isChecked))
        }
    }

    fun sendMessage(messageType: FFMessageType, message: String, customType: String = "", data: String = "", receiver: String? = null) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.MessageSendState(messageType, message, customType, data, receiver))
        }
    }

    fun start() {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.StartLiveState)
        }
    }

    fun sendChatEffect(chatEffect: ChatEffect) {
        onLaunch(Dispatchers.Main) {
            _streamAction.emit(StreamingState.EffectState(chatEffect))
        }
    }

    fun endChatEffect() {
        onLaunch(Dispatchers.Main) {
            // adjust delay time to show chat effect
            delay(5000)
            _streamAction.emit(StreamingState.MainState(MainAction.END_CHAT_EFFECT))
        }
    }

    fun updateLiveCount(viewCount: Long = 0) {
        onLaunch(Dispatchers.Main) {
            _streamLiveAction.emit(StreamingLiveState.UpdateCountState(viewCount))
        }
    }
}
