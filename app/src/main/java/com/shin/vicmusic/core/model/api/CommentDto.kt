package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val userId: String,
    val userName: String,
    val userHeadImg: String,
    val resourceType: String,
    val resourceId: String,
    val content: String,
    val likeCount: Int,
    val liked: Boolean,
    val createTime: Long,
    val parentId: String? = null,
    val rootId: String? = null,
    val replyCount: Int? = null,
    val replyList: List<CommentDto>? = null,
    val parentComment: CommentDto? = null
)
