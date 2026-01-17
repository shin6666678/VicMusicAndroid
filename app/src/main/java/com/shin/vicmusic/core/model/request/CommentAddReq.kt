package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CommentAddReq(
    val resourceType: String,
    val resourceId: String,
    val content: String,
    val parentId: String? = null // parentId 是可选的
)
