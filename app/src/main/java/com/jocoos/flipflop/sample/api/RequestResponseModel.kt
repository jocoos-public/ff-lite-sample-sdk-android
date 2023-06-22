package com.jocoos.flipflop.sample.api

class CreateUserRequest(val username: String, val password: String, val email: String)
class CreateUserResponse(val _id: String, val username: String, email: String)

class LoginRequest(val username: String, val password: String)
class LoginResponse(val accessToken: String, val fflTokens: FFLTokens)

class CreateChatTokenResponse(val chatToken: String, val appId: String, val userId: String, val userName: String)

class StreamKeyListResponse(val content: List<StreamInfo>)

class CreateVideoRoomRequest(val appUserId: String, val title: String = "Sample App Test", val type: String = "BROADCAST_RTMP", val description: String = "Sample App test", val accessLevel: String = "PUBLIC", val scheduledAt: String = "2023-12-19T23:59:59.999Z")
class CreateVideoRoomResponse(val id: Long, val state: String, val videoRoomState: String, val vodState: String, val vodUrl: String?, val member: Member, val liveStartedAt: String?, val title: String, val streamKey: StreamKey?, val liveUrl: String?, val chat: ChatInfo)

class CreateChatRoomResponse(val channelKey: String)

class VideoRoomList(val content: List<CreateVideoRoomResponse>)

class FFLTokens(val member: Member)
class Member(val id: Long, val appUserId: String, val appUserName: String)

class StreamInfo(val streamKey: String)
class StreamKey(val id: Long, val state: String, val streamKeyState: String)
class ChatInfo(val videoKey: String, val channelKey: String)
