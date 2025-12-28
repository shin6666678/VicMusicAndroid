package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val id: String?=null,
    val name: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    val sex: Int?=null,
    val points: Int?=null,
    val mail: String?=null,
    val followCount: Int?=null,
    val followerCount: Int?=null,
    val level: Int?=null,
    val vipLevel: Int?=null,
    val heardCount: Int?=null,
    val isFollowing: Boolean? = null,
    val isFollowingMe: Boolean? = null,

    val experience: Int? = 0,
    val nextLevelExp: Int? = 100,
    val totalListenTime: Long? = 0L

)