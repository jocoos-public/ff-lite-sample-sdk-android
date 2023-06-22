package com.jocoos.flipflop.sample.api

import com.jocoos.flipflop.sample.BuildConfig

class ApiManager {
    private val client = Client(BuildConfig.SAMPLE_SERVER_DOMAIN)
    private val apiService = client.getService(SampleServerService::class.java)

    companion object {
        private val apiManager = ApiManager()

        fun getInstance(): ApiManager {
            return apiManager
        }
    }

    suspend fun createUser(username: String, password: String, email: String): CreateUserResponse {
        val response = apiService.createUser(CreateUserRequest(username, password, email))
        return response.body()!!
    }

    suspend fun login(username: String, password: String): LoginResponse {
        val response = apiService.login(LoginRequest(username, password))
        val responseBody = response.body()!!
        client.setCredential(responseBody.accessToken)
        return responseBody
    }

    suspend fun chatToken(): CreateChatTokenResponse {
        val response = apiService.chatToken()
        return response.body()!!
    }

    suspend fun streamKey(memberId: Long): StreamInfo? {
        val response = apiService.streamKeys(memberId)
        val responseBody = response.body()!!
        if (responseBody.content.isEmpty()) {
            return null
        }
        return responseBody.content[0]
    }

    suspend fun createVideoRoom(appUserId: String, title: String): CreateVideoRoomResponse {
        val response = apiService.createVideoRoom(CreateVideoRoomRequest(appUserId, title))
        return response.body()!!
    }

    suspend fun getVideoRoom(videoRoomId: Long): CreateVideoRoomResponse {
        val response = apiService.getVideoRoom(videoRoomId)
        return response.body()!!
    }

    suspend fun getVideoRooms(): VideoRoomList {
        val response = apiService.getVideoRooms()
        return response.body()!!
    }

    suspend fun startBroadcast(videoRoomId: Long): CreateVideoRoomResponse {
        val response = apiService.startBroadcast(videoRoomId)
        return response.body()!!
    }

    suspend fun endBroadcast(videoRoomId: Long): CreateVideoRoomResponse {
        val response = apiService.endBroadcast(videoRoomId)
        return response.body()!!
    }

    suspend fun createChatRoom(videoRoomId: Long): CreateChatRoomResponse {
        val response = apiService.createChatRoom(videoRoomId)
        return response.body()!!
    }
}