package com.shin.vicmusic.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SongPageReq(
    val page: Int = 1,
    val size: Int = 20,
    val artistId: String? = null,
    val albumId: String? = null
)
@Serializable
data class AlbumPageReq(
    val page: Int = 1,
    val size: Int = 20,
    val artistId: String? = null,
)
@Serializable
data class AlbumDetailReq(
    val page: Int = 1,
    val size: Int = 20,
    val id: String,
)
@Serializable
data class ArtistPageReq(
    val page: Int = 1,
    val size: Int = 20,
    val region:String="全部",
    val type:String="全部",
    val style:String="全部"
)