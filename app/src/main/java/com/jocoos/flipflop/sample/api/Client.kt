package com.jocoos.flipflop.sample.api

import com.jocoos.flipflop.api.util.Jsons
import com.jocoos.flipflop.api.util.NullOnEmptyConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Client(baseUrl: String) {
    private val clientAdapter: Retrofit
    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private val userAgentInterceptor: UserAgentInterceptor
    private val userAuthInterceptor: UserBasicAuthInterceptor

    init {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        userAgentInterceptor = UserAgentInterceptor()
        userAuthInterceptor = UserBasicAuthInterceptor()

        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(userAuthInterceptor)
            .connectTimeout(10000, TimeUnit.MILLISECONDS) // 10 seconds
            .build()
        clientAdapter = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(Jsons.gson()))
            .build()
    }

    fun setCredential(apiKey: String, apiSecret: String) {
        userAuthInterceptor.setCredential(apiKey, apiSecret)
    }

    fun <T> getService(clazz: Class<T>): T {
        return clientAdapter.create(clazz)
    }
}
