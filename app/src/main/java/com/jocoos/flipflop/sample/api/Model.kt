package com.jocoos.flipflop.sample.api

import com.jocoos.flipflop.api.model.VideoRoomResponse

class VideoRooms(val content: List<VideoRoomResponse>)

class TokenRequest(val appUserId: String, val appUserName: String, val appUserProfileImgUrl: String? = null)

class Token(val accessToken: String, val streamingToken: String)
