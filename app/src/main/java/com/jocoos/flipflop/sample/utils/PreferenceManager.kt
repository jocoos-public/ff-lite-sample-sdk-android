package com.jocoos.flipflop.sample.utils

import android.content.Context
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    companion object {
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"

        const val KEY_STREAMING_INFO = "streaming-info"
        const val KEY_LIVE_WATCH_INFO = "live-watch-info"
        const val KEY_VOD_INFO = "vod-info"
        const val KEY_VIDEO_INFO = "video-info"
        const val KEY_STREAM_KEY = "streak-key"
        const val KEY_LIVE_URL = "live-url"
        const val KEY_VOD_URL = "vod-url"

        const val testPassword = "password"
        const val testEmail = "test@test.com"
    }

    private val sharedPreferences = context.getSharedPreferences("local_data", Context.MODE_PRIVATE)

    var userId: String
    var username: String
    var password: String

    init {
        userId = sharedPreferences.getString(KEY_USER_ID, "")!!
        username = sharedPreferences.getString(KEY_USERNAME, "")!!
        password = sharedPreferences.getString(KEY_PASSWORD, testPassword)!!
    }

    fun setUserInfo(userId: String, username: String, password: String) {
        this.userId = userId
        this.username = username
        this.password = password

        sharedPreferences.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
        }
    }
}