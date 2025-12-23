package com.shin.vicmusic.core.domain

import com.shin.vicmusic.core.model.response.NetworkPageData
import kotlinx.serialization.Serializable


/**
 * 专辑领域模型 (Domain Model)
 */
@Serializable
data class Album (
    val id:String,
    val title:String="未知专辑",
    val artist: Artist=Artist(id = "-1", name = "未知歌手"),
    val icon:String="",
    val description:String="",
    val company:String="",
    val releaseTime:String="",
    val style:String="",
    val songCount:Int=0,
    val isLiked: Boolean = false,

)
@Serializable
data class AlbumDetail(
    val album: Album,
    val songs: PageResult<Song>
)