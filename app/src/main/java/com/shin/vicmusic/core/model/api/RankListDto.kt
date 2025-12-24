package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class RankListPeakDto(
    val id: String,
    val title: String,
    val type: String,
    val icon: String, // [修改] 对应 JSON 中的 "icon"
    val updateFrequency: String,
    val status: Int,
    val top3: List<RankListTop3Dto> // [修改] 对应 JSON 中的 "top3"
)

@Serializable
data class RankListTop3Dto(
    val songId: String,
    val title: String,
    val artistName: String
)
@Serializable
data class RankListDetailDto(
    val id: String,
    val title: String,
    val icon: String,
    val songs: List<SongListItemDto>
)