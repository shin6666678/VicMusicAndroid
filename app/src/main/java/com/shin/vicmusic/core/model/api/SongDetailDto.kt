package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

/**
 * 歌曲详情 API DTO，对应后端详情接口的 data 字段
 */
@Serializable
data class SongDetailDto(
    val id: String,
    val title: String?=null,
    val artist: ArtistDto?=null,
    val album: AlbumDto?=null,
    val payType: PayTypeDto?=null,
    val uri: String?=null,
    val icon: String?=null,
    val genre: String?=null,
    val lyric: String?=null,
    val lyricStyle: Int?=null,
    val likesCount: Int?=null,
    val clicksCount: Int?=null,
    val commentsCount: Int?=null,
    val isLiked: Boolean?=null
)