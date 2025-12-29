package com.shin.vicmusic.core.model.api

import com.shin.vicmusic.core.model.response.NetworkPageData
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val name: String?=null,
    val image: String?=null,
    val description: String?=null,
    val followerCount: Int?=null,
    val isFollowing: Boolean?=null,
    val region: String?=null,
    val type: String?=null,
    val style: String?=null
)
@Serializable
data class ArtistDetailResp(
    val id: String,
    val name: String?=null,
    val image: String?=null,
    val description: String?=null,
    val followerCount: Int?=null,
    val isFollowing: Boolean?=null,
    val region: String?=null,
    val type: String?=null,
    val style: String?=null,
    val songs: NetworkPageData<SongListItemDto>?=null
)