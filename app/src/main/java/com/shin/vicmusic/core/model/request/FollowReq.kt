package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class FollowReq(
    val targetId:String,
    val targetType:Int
)