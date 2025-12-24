package com.shin.vicmusic.core.model.request

data class PlaylistSongReq(
    val playlistId: String,
    val songId: String
)