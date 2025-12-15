package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val image: String? = null
)