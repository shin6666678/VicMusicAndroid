package com.shin.vicmusic.core.domain

data class RankListPeak(
    val id: String,
    val imageUrl: String,
    val title: String,
    val updateFrequency: String, // [新增] 更新频率
    val items: List<RankTopItem> // [修改] 明确类型，不再是 Any?
)
data class RankTopItem(
    val id: String,
    val title: String,
    val artist: String
)
data class RankListDetail(
    val id:String,
    val imageUrl: String,
    val title: String,
    val items: List<Song>
)