package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterReq(
    val name: String,
    val pwd: String,
    val headImg: String,
    val mail: String,
    val code: String
)
