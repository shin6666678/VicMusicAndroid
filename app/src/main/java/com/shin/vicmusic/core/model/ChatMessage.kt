package com.shin.vicmusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String? = null,
    val fromUserId: String,
    val toUserId: String,
    val content: String,
    val createTime: Long? = null, // 后端传回的时间戳
    val status: Int = 0
)