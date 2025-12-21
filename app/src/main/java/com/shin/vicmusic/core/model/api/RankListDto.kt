package com.shin.vicmusic.core.model.api

data class RankListPeakDto(
    val id: String,
    val title: String,
    val imageUrl: String,
    val items: List<Any>
)