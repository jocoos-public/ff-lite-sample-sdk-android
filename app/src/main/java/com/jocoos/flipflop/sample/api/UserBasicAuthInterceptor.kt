package com.jocoos.flipflop.sample.api

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class UserBasicAuthInterceptor : Interceptor {
    private var clientValue =
        "Basic " + Base64.encodeToString("2Md55V7zWNBU4blXVZZ2AF2k4a5:2Md55VPQ9Qd6z5Bw7EzMHzLAo0f".toByteArray(), Base64.NO_WRAP)

    fun setCredential(apiKey: String, apiSecret: String) {
        clientValue = "Basic " + Base64.encodeToString("$apiKey:$apiSecret".toByteArray(), Base64.NO_WRAP)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .addHeader("Authorization", clientValue)
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}
