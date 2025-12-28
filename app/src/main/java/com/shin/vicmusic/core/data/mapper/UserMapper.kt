package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.model.api.UserInfoDto

fun UserInfoDto.toDomain(): UserInfo {
    return UserInfo(
        id = this.id ?: "",
        name = this.name ?: "未命名用户",
        headImg = this.headImg ?: "",
        slogan = this.slogan ?: "",
        sex = this.sex ?: 2,
        points = this.points ?: 0,
        mail = this.mail ?: "",
        followCount = this.followCount ?: 0,
        followerCount = this.followerCount ?: 0,
        level = this.level ?: 0,
        vipLevel = this.vipLevel ?: 0,
        heardCount = this.heardCount ?: 0,
        isFollowing = this.isFollowing ?: false,
        isFollowingMe = this.isFollowingMe ?: false,
        experience = experience ?: 0,
        nextLevelExp = nextLevelExp ?: 100,
        totalListenTime = totalListenTime ?: 0L
    )
}