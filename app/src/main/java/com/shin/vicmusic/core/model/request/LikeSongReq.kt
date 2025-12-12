package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LikeSongReq(
    val songId: String
)