package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.api.UserInfoDto

fun UserInfoDto.toDomain(): User{
    return User(
        id = this.id,
        name = this.name,
        headImg = this.headImg,
        slogan = this.slogan,
        sex = this.sex,
        points = this.points,
        mail = this.mail,
        followCount = this.followCount,
        followerCount = this.followerCount,
        level = this.level,
        vipLevel = this.vipLevel,
        heardCount = this.heardCount
    )
}