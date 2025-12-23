package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val title: String,
    val icon: String? = null
)