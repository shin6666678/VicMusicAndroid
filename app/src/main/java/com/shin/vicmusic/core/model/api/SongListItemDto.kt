package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class SongListItemDto(
    val id: String,
    val title: String?=null,
    val artist: ArtistDto?=null,
    val album: AlbumDto?=null,
    val payType: PayTypeDto?=null,
    val icon: String?=null,
    val uri:String?=null,
    val lyric:String?=null,
    val isLiked: Boolean?=null,
    val playCount:Int?=null,
    val isCopyright: Int?=null,
    val externalUrls: String?=null,
    val disclaimer: String?=null
)