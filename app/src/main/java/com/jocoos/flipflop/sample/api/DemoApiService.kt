package com.jocoos.flipflop.sample.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DemoApiService {
    @POST("/v2/apps/me/members/login")
    suspend fun token(@Body request: TokenRequest): Response<Token>

    @GET("/v2/apps/me/video-rooms")
    suspend fun videoRooms(@Query("pageSize") pageSize: Int = 20): Response<VideoRooms>
}
