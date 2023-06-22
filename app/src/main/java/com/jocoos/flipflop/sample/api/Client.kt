package com.jocoos.flipflop.sample.api

import com.jocoos.flipflop.api.util.Jsons
import com.jocoos.flipflop.api.util.NullOnEmptyConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Client(baseUrl: String) {
    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private val authInterceptor = AuthInterceptor()
    private val clientAdapter: Retrofit

    init {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(10000, TimeUnit.MILLISECONDS) // 10 seconds
            .build()

        clientAdapter = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setCredential(accessToken: String?) {
        authInterceptor.setCredential(accessToken)
    }

    fun <T> getService(clazz: Class<T>?): T {
        return clientAdapter.create(clazz)
    }
}
