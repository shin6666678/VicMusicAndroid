package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val cover: String="",
    val description: String="",
    val songCount: Int = 0,
    val playCount: Int = 0,
    val likeCount: Int = 0,
    val isPublic: Int? = 0,
    val isLiked: Boolean = false,

    val ownerName: String="",
)

@Serializable
data class PlaylistDetail(
    val info: Playlist,
    val songs: List<Song> // 复用现有的 Song Domain
)
