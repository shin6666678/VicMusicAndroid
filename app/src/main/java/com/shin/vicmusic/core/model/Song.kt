package com.shin.vicmusic.core.model

import com.shin.vicmusic.util.Constant
import kotlinx.serialization.Serializable

/**
 * 歌曲
 */
@Serializable
data class Song (
    val id:String,
    val title:String,
    val uri: String,
    val icon:String="",
    val album:String="未知专辑(服务端未返回)",
    val artist:String="未知艺术家(服务端未返回)",
    val genre:String,
    val lyricStyle:Int= Constant.VALUE0,
    val lyric:String="",
    val trackNumber: Int=1,
    val totalTrackCount:Int=1

)