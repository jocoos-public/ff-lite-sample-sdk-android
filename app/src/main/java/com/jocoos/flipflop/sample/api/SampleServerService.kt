package com.jocoos.flipflop.sample.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SampleServerService {
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/auth/chat-token")
    suspend fun chatToken(): Response<CreateChatTokenResponse>

    @GET("/stream-keys")
    suspend fun streamKeys(@Query("memberId") memberId: Long): Response<StreamKeyListResponse>

    @POST("/video-rooms")
    suspend fun createVideoRoom(@Body request: CreateVideoRoomRequest): Response<CreateVideoRoomResponse>

    @GET("/video-rooms/{video_room_id}")
    suspend fun getVideoRoom(@Path("video_room_id") videoRoomId: Long): Response<CreateVideoRoomResponse>

    @GET("/video-rooms")
    suspend fun getVideoRooms(): Response<VideoRoomList>

    @POST("/video-rooms/{video_room_id}/start-broadcast")
    suspend fun startBroadcast(@Path("video_room_id") videoRoomId: Long): Response<CreateVideoRoomResponse>

    @POST("/video-rooms/{video_room_id}/end-broadcast")
    suspend fun endBroadcast(@Path("video_room_id") videoRoomId: Long): Response<CreateVideoRoomResponse>

    @POST("/video-rooms/{video_room_id}/chat-room")
    suspend fun createChatRoom(@Path("video_room_id") videoRoomId: Long): Response<CreateChatRoomResponse>
}
