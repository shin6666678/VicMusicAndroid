package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class RecommendCardDto(
    val title: String,
    val songs: List<SongListItemDto> = emptyList()
)