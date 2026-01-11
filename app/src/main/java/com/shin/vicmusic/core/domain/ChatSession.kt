package com.shin.vicmusic.core.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    @SerialName("userId")
    val userId: String,
    val username: String,
    val avatar: String? = null,
    val lastMessage: String? = null,
    val lastTime: Long? = null,
    val unreadCount: Int = 0
)