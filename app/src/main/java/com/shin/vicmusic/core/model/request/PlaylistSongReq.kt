package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSongReq(
    val playlistId: String,
    val songId: String
)