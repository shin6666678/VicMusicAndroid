package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val title: String,
    val icon: String? = null
)
// 列表展示通常只需要 id, title, icon