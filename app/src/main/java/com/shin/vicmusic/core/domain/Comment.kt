package com.shin.vicmusic.core.domain

/**
 * 评论的领域模型，供 UI 层使用
 */
data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userHeadImg: String,
    val resourceType: String,
    val resourceId: String,
    val content: String,
    val likeCount: Int,
    var liked: Boolean,
    val createTime: Long
)
