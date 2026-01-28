package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String?=null,
    val name: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    val level: Int?=null,
    val vipLevel: Int?=null,
    val isFollowing: Boolean? = null,
    val isFollowingMe: Boolean? = null,
)

@Serializable
data class UserDetailDto(
    val id: String?=null,
    val name: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    val sex: Int?=null,
    val bgImg: String?=null,
    val points: Int?=null,
    val mail: String?=null,
    val followCount: Int?=null,
    val followerCount: Int?=null,
    val level: Int?=null,
    val vipLevel: Int?=null,
    val heardCount: Int?=null,
    val isFollowing: Boolean? = null,
    val isFollowingMe: Boolean? = null,

    val experience: Int? = null,
    val nextLevelExp: Int? = null,
    val totalListenTime: Long? = null

)