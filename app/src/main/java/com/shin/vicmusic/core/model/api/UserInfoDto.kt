package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val id: Long?=null,
    val name: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    /**
     * 0 表示女 (Female)，1 表示男 (Male)
     */
    val sex: Int?=null,
    val points: Int?=null,
    val mail: String?=null,
    val followCount: Int?=null,
    val followerCount: Int?=null,
    val level: Int?=null,
    val vipLevel: Int?=null,
    val heardCount: Int?=null

)