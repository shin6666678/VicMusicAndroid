package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val type: Int, // 0:System 1:Like 2:Comment
    val isRead: Boolean,
    val createTime: String? = null
)