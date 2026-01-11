package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LikeReq(
    val targetId: String,
    val type:Int
)