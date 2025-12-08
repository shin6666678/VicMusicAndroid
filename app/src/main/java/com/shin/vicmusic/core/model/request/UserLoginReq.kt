package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginReq(
    val mail: String,
    val password: String
)