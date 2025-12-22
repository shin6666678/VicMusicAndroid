package com.shin.vicmusic.core.model.request

data class FollowReq(
    val targetId:String,
    val targetType:Int
)