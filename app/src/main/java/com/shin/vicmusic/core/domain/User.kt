package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long?=null, // 主键ID
    val name: String?=null,
    val pwd: String?=null,
    val headImg: String?=null,
    val slogan: String?=null,
    val sex: Int?=null,
    val points: Int?=null,
    val mail: String?=null,
    val followCount: Int? = null,
    val followerCount: Int? = null,
    val level: Int? = null,
    val vipLevel: Int? = null,
    val heardCount: Int? = null

){
    fun isVip(): Boolean {
        return vipLevel != null && vipLevel != 0
    }
}