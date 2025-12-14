package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class SongListItemDto(
    val id: String,
    val title: String,
    val artist: ArtistDto, // 嵌套对象
    val album: AlbumDto,   // 嵌套对象
    val payType: PayTypeDto, // 嵌套对象
    val icon: String? = null,
    val isLiked: Boolean = false // 列表接口返回的字段
)