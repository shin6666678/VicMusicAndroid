package com.shin.vicmusic.core.domain

data class Playlist(
    val id: String,
    val name: String,
    val songIds: List<String> = emptyList(), // 仅存储ID
)
