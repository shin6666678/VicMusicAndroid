package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class SearchComprehensiveDto(
    val songs: List<SongListItemDto>? = null,
    val playlists: List<PlaylistDto>? = null,
    val albums: List<AlbumDto>? = null,
    val artists: List<ArtistDto>? = null,
    val users: List<UserInfoDto>? = null
)