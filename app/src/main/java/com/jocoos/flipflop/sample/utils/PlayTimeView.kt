package com.jocoos.flipflop.sample.utils

import android.content.Context
import android.util.AttributeSet
import java.util.*

class PlayTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    companion object {
        private const val TIME_UNSET = Long.MIN_VALUE + 1
        const val TIME_UNLIMITED = Long.MAX_VALUE - 1
    }

    private var formatBuilder = StringBuilder()
    private var formatter = Formatter(formatBuilder, Locale.getDefault())
    private var startTime: Long = 0L
    private var updateProgressAction: Runnable

    private var elapsedTime: Long = TIME_UNLIMITED
    private var timeListener: ((elapsedTime: Long) -> Unit)? = null

    init {
        updateProgressAction = Runnable { updateProgress() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(updateProgressAction)
    }

    fun start(liveStartTime: Long) {
        this.startTime = liveStartTime
        updateProgress()
    }

    fun setTimeListener(elapsedTime: Long, timeListener: ((elapsedTime: Long) -> Unit)? = null) {
        this.elapsedTime = elapsedTime * 1000 + 2
        this.timeListener = timeListener
    }

    private fun updateProgress() {
        val elapsedTime = System.currentTimeMillis() - startTime
        text = getStringForTime(formatBuilder, formatter, elapsedTime)
        if (timeListener != null && elapsedTime > this.elapsedTime) {
            timeListener?.invoke(elapsedTime)
        }

        removeCallbacks(updateProgressAction)

        val delayMs = 1000L
        postDelayed(updateProgressAction, delayMs)
    }

    private fun getStringForTime(builder: StringBuilder, formatter: Formatter, timeMs: Long): String {
        var timeMs = timeMs
        if (timeMs == TIME_UNSET) {
            timeMs = 0
        }
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        builder.setLength(0)
        return if (hours > 0) {
            formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            formatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}
