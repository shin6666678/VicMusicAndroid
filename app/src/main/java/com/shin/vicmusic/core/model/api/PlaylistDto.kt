package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: String,
    val userId: String,
    val name: String,
    val cover: String?,
    val description: String?,
    val playCount: Int
)
@Serializable
data class PlaylistDetailDto(
    val info: PlaylistDto,
    val songs: List<SongListItemDto>
)