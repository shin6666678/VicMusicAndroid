package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String="",
    val name: String="未命名用户",
    val pwd: String="",
    val headImg: String="",
    val mail: String="",
)