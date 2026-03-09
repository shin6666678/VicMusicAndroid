package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.model.api.PlaylistDetailDto
import com.shin.vicmusic.core.model.api.PlaylistDto

fun PlaylistDto.toDomain(): Playlist {
    return Playlist(
        id = id,
        ownerId = ownerId ?:"",
        name = name ?: "",
        cover = cover?:"",
        description = description?:"",
        songCount = songCount ?: 0,
        playCount = playCount ?: 0,
        likeCount = likeCount ?: 0,
        isPublic = isPublic ?: 0,
        ownerName = ownerName ?: "",
        isLiked = isLiked ?: false
    )
}

fun PlaylistDetailDto.toDomain(): PlaylistDetail {
    return PlaylistDetail(
        info = info.toDomain(),
        songs = songs.map { it.toDomain() }
    )
}