package com.jocoos.flipflop.sample.api

import com.jocoos.flipflop.FFLErrorCode
import com.jocoos.flipflop.FlipFlopException

class DemoService(
    apiKey: String,
    apiSecret: String,
    baseUrl: String
) {
    private val client = Client(baseUrl)
    private val apiService = client.getService(DemoApiService::class.java)

    init {
        client.setCredential(apiKey, apiSecret)
    }

    suspend fun accessToken(userId: String, username: String): Result<Token> {
        return try {
            val response = apiService.token(TokenRequest(userId, username))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                // handle unsuccessful response
                Result.failure(FlipFlopException(FFLErrorCode.SERVER_UNEXPECTED_ERROR, "unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(FlipFlopException(FFLErrorCode.SERVER_UNEXPECTED_ERROR, e.message ?: "unknown error"))
        }
    }

    suspend fun videoRooms(): Result<VideoRooms> {
        return try {
            val response = apiService.videoRooms()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                // handle unsuccessful response
                Result.failure(FlipFlopException(FFLErrorCode.SERVER_UNEXPECTED_ERROR, "unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(FlipFlopException(FFLErrorCode.SERVER_UNEXPECTED_ERROR, e.message ?: "unknown error"))
        }
    }
}