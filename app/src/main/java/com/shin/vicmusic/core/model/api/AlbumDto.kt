package com.shin.vicmusic.core.model.api

import com.shin.vicmusic.core.model.response.NetworkPageData
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val title: String?=null,
    val icon: String?=null
)
@Serializable
data class AlbumDetailResp(
    val id:String,
    val artist: ArtistDto?=null,
    val title: String?=null,
    val icon: String?=null,
    val description: String?=null,
    val company: String?=null,
    val releaseTime: String?=null,
    val style: String?=null,
    val songCount: Int?=null,
    val isLiked: Boolean?=null,
    val songs: NetworkPageData<SongListItemDto>?=null
)