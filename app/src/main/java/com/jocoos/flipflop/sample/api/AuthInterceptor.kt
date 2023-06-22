package com.jocoos.flipflop.sample.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {
    private var accessToken: String? = null

    fun setCredential(accessToken: String?) {
        this.accessToken = accessToken
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        accessToken?.let {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        }
        return chain.proceed(request)
    }
}
