package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.model.api.PlaylistDetailDto
import com.shin.vicmusic.core.model.api.PlaylistDto

fun PlaylistDto.toDomain(): Playlist {
    return Playlist(
        id = id,
        userId = userId,
        name = name,
        cover = cover,
        description = description,
        playCount = playCount
    )
}

fun PlaylistDetailDto.toDomain(): PlaylistDetail {
    return PlaylistDetail(
        info = info.toDomain(),
        // 复用 SongMapper 中的 toDomain()
        songs = songs.map { it.toDomain() }
    )
}