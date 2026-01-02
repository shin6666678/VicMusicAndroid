package com.shin.vicmusic.core.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSessionDto(
    @SerialName("userId")
    val userId: String,
    val username: String,
    val avatar: String? = null,
    val lastMessage: String? = null,
    val lastTime: String? = null,
    val unreadCount: Int = 0
)