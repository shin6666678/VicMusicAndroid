package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val name: String,
    val image: String? = null
)