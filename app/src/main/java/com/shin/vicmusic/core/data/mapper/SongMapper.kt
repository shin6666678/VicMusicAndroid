package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.*
import com.shin.vicmusic.core.model.api.*
import kotlin.collections.map

// --- 基础转换扩展函数 ---

fun PayTypeDto.toDomain(): PayType {
    return PayType.fromValue(this.value)
}
// --- Song 转换函数 ---

/**
 * 将 SongListItemDTO (API DTO for List) 转换为 Song 领域模型
 */
fun SongListItemDto.toDomain(): Song {
    return Song(
        id = this.id,
        title = this.title?:"",
        uri = this.uri?:"",
        icon = this.icon?:"",
        album = this.album?.toDomain() ?: Album(id = "", title = "未知专辑"),
        artist = this.artist?.toDomain() ?: Artist(id = "", name = "未知歌手"),
        payType = this.payType?.toDomain()?: PayType.PAY,
        genre = "未知", // 列表接口未返回，填充默认值
        lyricStyle = 0,
        lyric = this.lyric?:"",
        isLiked = this.isLiked?:false,
        likesCount = 0,
        clicksCount = 0,
        commentsCount = 0,
    )
}

/**
 * 将 SongDetailDTO (API DTO for Detail) 转换为 Song 领域模型
 */
fun SongDetailDto.toDomain(): Song {
    return Song(
        id = this.id,
        title = this.title?:"",
        uri = this.uri?:"",
        icon = this.icon?:"",
        album = this.album?.toDomain() ?: Album(id = "", title = "未知专辑"),
        artist = this.artist?.toDomain() ?: Artist(id = "", name = "未知歌手"),
        payType = this.payType?.toDomain()?: PayType.PAY,
        genre = this.genre ?: "未知",
        lyricStyle = this.lyricStyle ?: 0,
        lyric = this.lyric?:"",
        isLiked = this.isLiked?:false,
        likesCount = this.likesCount ?: 0,
        clicksCount = this.clicksCount ?: 0,
        commentsCount = this.commentsCount ?: 0,
    )
}

// --- 列表转换扩展函数 ---

fun List<SongListItemDto>.toDomainList(): List<Song> {
    return this.map { it.toDomain() }
}