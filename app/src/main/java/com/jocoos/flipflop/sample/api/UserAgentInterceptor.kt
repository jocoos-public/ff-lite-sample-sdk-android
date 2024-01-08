package com.jocoos.flipflop.sample.api

import android.os.Build
import com.jocoos.flipflop.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class UserAgentInterceptor : Interceptor {
    val DEFAULT_USER_AGENT = String.format("FlipFlop SDK v%s (Android API %s)",
        BuildConfig.VERSION_NAME, Build.VERSION.RELEASE)

    private var userAgent: String

    init {
        this.userAgent = DEFAULT_USER_AGENT
    }

    fun setUserAgent(userAgent: String) {
        this.userAgent = userAgent
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("User-Agent", userAgent)
            .build()
        return chain.proceed(newRequest)
    }
}
