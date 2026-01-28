package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PublishFeedReq(
    val targetId: String? = null,
    val targetType: String? = null,
    val comment: String
)
