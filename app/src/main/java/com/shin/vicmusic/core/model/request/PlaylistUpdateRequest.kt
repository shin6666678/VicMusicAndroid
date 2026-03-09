package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistUpdateRequest (
    val id: String,
    val name: String?=null,
    val description: String?=null,
    val cover: String?=null,
    val isPublic: Int?=null,
)