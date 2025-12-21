package com.shin.vicmusic.core.domain

data class RankListPeak(
    val id:String,
    val imageUrl: String,
    val title: String,
    val items: List<Any?>
)
data class RankListDetail(
    val id:String,
    val imageUrl: String,
    val title: String,
    val items: List<Any?>
)