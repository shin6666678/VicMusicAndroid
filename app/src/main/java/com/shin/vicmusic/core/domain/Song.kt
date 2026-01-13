package com.shin.vicmusic.core.domain

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.shin.vicmusic.util.Constant
import com.shin.vicmusic.util.ResourceUtil
import kotlinx.serialization.Serializable
import androidx.core.net.toUri

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
    val albumName:String="",
    val artist: Artist=Artist(id = "-1"),
    val artistName:String="",
    val payType: PayType= PayType.FREE,
    val genre:String="",
    val lyricStyle:Int= Constant.VALUE0,
    val lyric:String="",
    val lyricList: List<LyricLine> = emptyList(),
    val likesCount: Int=0,
    val clicksCount: Int=0,
    val commentsCount: Int=0,
    val uploaderUserId: String="",
    val duration: Long = 0,

    val isLiked: Boolean = false,
    val playCount:Int=0
){
    /**
     * 将 Song 转换为 ExoPlayer 需要的 MediaItem
     */
    fun toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(this.title)
            .setArtist(this.artistName)
            .setAlbumTitle(this.albumName)
            .setArtworkUri(this.icon.toUri())
            .build()

        return MediaItem.Builder()
            .setUri(ResourceUtil.r2(this.uri))
            .setMediaId(this.id) // [关键] 这个 ID 是让 Service 和 Manager 同步的关键
            .setMediaMetadata(metadata)
            .build()
    }
}
@Serializable
data class LyricLine(val time: Long, val content: String)