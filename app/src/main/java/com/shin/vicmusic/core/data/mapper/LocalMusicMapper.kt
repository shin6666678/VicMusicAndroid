package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.database.entity.LocalMusicEntity
import com.shin.vicmusic.core.domain.Song

fun LocalMusicEntity.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artistName = artistName,
        uri = uri,
        albumName = albumName ?: "",
        duration = duration,
        icon = icon ?: ""
    )
}

fun Song.toEntity(): LocalMusicEntity {
    return LocalMusicEntity(
        id = id,
        title = title,
        artistName = artistName,
        uri = uri,
        albumName = albumName,
        duration = duration,
        icon = icon
    )
}
