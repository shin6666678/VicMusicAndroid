package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String?=null,
    val followerCount: Int = 0,
    val isFollowing: Boolean = false,
    val region: String = "全部",
    val type: String = "全部",
    val style: String = "全部"
)
