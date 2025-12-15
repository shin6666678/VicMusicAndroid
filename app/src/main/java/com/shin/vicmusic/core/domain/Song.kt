package com.shin.vicmusic.core.domain // 建议将 core.model 迁移至 core.domain

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
    val title:String,
    val uri: String?,
    val icon:String?,
    val album: Album,          // <<-- 嵌套 Album 领域对象
    val artist: Artist,        // <<-- 嵌套 Artist 领域对象
    val payType: PayType,      // <<-- 使用 PayType 枚举
    val genre:String,
    val lyricStyle:Int= Constant.VALUE0,
    val lyric:String?,
    val isLiked: Boolean = false,
    val likesCount: Int=0,
    val clicksCount: Int=0,
    val commentsCount: Int=0,
    val uploaderUserId: String?=null,
    val createdAt: String?=null,
    val updatedAt: String?=null

){
    /**
     * 将 Song 转换为 ExoPlayer 需要的 MediaItem
     */
    fun toMediaItem(): MediaItem {
        return MediaItem.fromUri(ResourceUtil.r2(this.uri ?: ""))
    }
}