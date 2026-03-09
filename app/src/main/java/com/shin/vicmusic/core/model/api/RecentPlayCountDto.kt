package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class RecentPlayCountDto(
    val count: Int,
    val uri: String
)