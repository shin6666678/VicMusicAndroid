package com.shin.vicmusic.core.domain

import androidx.media3.common.MediaItem
import com.shin.vicmusic.util.Constant
import com.shin.vicmusic.util.ResourceUtil
import kotlinx.serialization.Serializable

/**
 * 歌曲领域模型 (Domain Model) - 用于业务逻辑层和 UI 层
 */
@Serializable
data class Song (
    val id:String,
    val title:String="",
    val uri: String="",
    val icon:String="",
    val album: Album=Album(id = "-1"),
    val artist: Artist=Artist(id = "-1"),
    val payType: PayType= PayType.FREE,
    val genre:String="",
    val lyricStyle:Int= Constant.VALUE0,
    val lyric:String="",
    val lyricList: List<LyricLine> = emptyList(),
    val isLiked: Boolean = false,
    val likesCount: Int=0,
    val clicksCount: Int=0,
    val commentsCount: Int=0,
    val uploaderUserId: String="",
){
    /**
     * 将 Song 转换为 ExoPlayer 需要的 MediaItem
     */
    fun toMediaItem(): MediaItem {
        return MediaItem.fromUri(ResourceUtil.r2(this.uri ?: ""))
    }
}
@Serializable
data class LyricLine(val time: Long, val content: String)