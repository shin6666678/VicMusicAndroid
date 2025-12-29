package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.model.api.ArtistDetailResp
import com.shin.vicmusic.core.model.api.ArtistDto

fun ArtistDto.toDomain(): Artist {
    return Artist(
        id = this.id,
        name = this.name?:"未知艺术家",
        image = this.image?:"",
        description = this.description?:"",
        followerCount = this.followerCount?:0,
        isFollowing = this.isFollowing?:false,
        region = this.region?:"未知",
        type = this.type?:"未知",
        style = this.style?:"未知"

    )
}
fun ArtistDetailResp.toDomain(): Artist {
    return Artist(
        id = this.id,
        name = this.name?:"未知艺术家",
        image = this.image?:"",
        description = this.description?:"",
        followerCount = this.followerCount?:0,
        isFollowing = this.isFollowing?:false,
        region = this.region?:"未知",
        type = this.type?:"未知",
        style = this.style?:"未知"
    )
}