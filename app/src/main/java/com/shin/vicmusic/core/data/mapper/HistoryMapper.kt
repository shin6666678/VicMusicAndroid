package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.RecentPlayCount
import com.shin.vicmusic.core.model.api.RecentPlayCountDto

fun RecentPlayCountDto.toDomain(): RecentPlayCount {
    return RecentPlayCount(
        count = this.count,
        uri = this.uri
    )
}