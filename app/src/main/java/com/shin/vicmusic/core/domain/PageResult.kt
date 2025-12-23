package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class PageResult<T> (
    val items: List<T>,
    val total: Int,
    val page: Int,
    val hasMore: Boolean
)