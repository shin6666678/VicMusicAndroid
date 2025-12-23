package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.model.api.AlbumDto

fun AlbumDto.toDomain(): Album {
    return Album(
        id = id,
        title = title,
        icon = icon
    )
}