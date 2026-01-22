package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String="",
    val name: String="未命名用户",
    val headImg: String="",
    val slogan: String="",
    val sex: Int=2,
    val bgImg: String="",
    val points: Int=0,
    val mail: String="",
    val followCount: Int = 0,
    val followerCount: Int = 0,
    val level: Int = 0,
    val vipLevel: Int = 0,
    val heardCount: Int = 0,
    val isFollowing: Boolean = false,
    val isFollowingMe: Boolean = false,

    val experience: Int=0,
    val nextLevelExp: Int=0,
    val totalListenTime: Long=0
){
    fun isVip(): Boolean {
        return vipLevel != 0
    }
}