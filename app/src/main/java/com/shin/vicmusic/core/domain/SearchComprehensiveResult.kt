package com.shin.vicmusic.core.domain

data class SearchComprehensiveResult(
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val users: List<UserInfo> = emptyList()
)