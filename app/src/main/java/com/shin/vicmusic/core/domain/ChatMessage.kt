package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String? = null,
    val fromUserId: String,
    val toUserId: String,
    val content: String,
    val createTime: Long? = null,
    val status: Int = 0
)