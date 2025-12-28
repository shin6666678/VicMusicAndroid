package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: String,
    val name: String?=null,
    val cover: String?=null,
    val description: String?=null,
    val songCount: Int? = null,
    val playCount: Int? = null,
    val likeCount: Int? = null,
    val isPublic: Int? = null,

    val ownerName: String?=null,
)
@Serializable
data class PlaylistDetailDto(
    val info: PlaylistDto,
    val songs: List<SongListItemDto> = emptyList()
)