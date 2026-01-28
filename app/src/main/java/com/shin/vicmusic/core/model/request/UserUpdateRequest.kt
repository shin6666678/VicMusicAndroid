package com.shin.vicmusic.core.model.request;

import kotlinx.serialization.Serializable;

@Serializable
data class UserUpdateRequest(
    val name: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    val sex: Int?=null,
    val bgImg: String?=null,

)
