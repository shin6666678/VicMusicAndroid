package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.model.api.AlbumDetailResp
import com.shin.vicmusic.core.model.api.AlbumDto
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.response.NetworkPageData

fun AlbumDto.toDomain(): Album {
    return Album(
        id = id,
        title = title?:"未知专辑",
        icon = icon?:""
    )
}

fun AlbumDetailResp.toDomain(): Album {
    return Album(
        id = id,
        title = title?:"未知专辑",
        artist = artist?.toDomain()?: Artist(id = "-1", name = "未知歌手"),
        icon = icon?:"",
        description = description?:"",
        company = company?:"",
        releaseTime = releaseTime?:"",
        style = style?:"",
        songCount = songCount?:0,
        isLiked = isLiked?:false,
    )
}