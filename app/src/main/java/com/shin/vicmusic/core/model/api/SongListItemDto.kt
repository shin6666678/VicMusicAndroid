package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class SongListItemDto(
    val id: String,
    val title: String,
    val artist: ArtistDto=ArtistDto("0","未知"), // 嵌套对象
    val album: AlbumDto=AlbumDto("0","未知"),   // 嵌套对象
    val payType: PayTypeDto=PayTypeDto(0,"未知"), // 嵌套对象
    val icon: String? = null,
    val uri:String?=null,
    val isLiked: Boolean = false // 列表接口返回的字段
)