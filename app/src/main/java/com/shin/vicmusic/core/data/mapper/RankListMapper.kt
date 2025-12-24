package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.RankListDetail
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.domain.RankTopItem
import com.shin.vicmusic.core.model.api.RankListDetailDto
import com.shin.vicmusic.core.model.api.RankListPeakDto
import com.shin.vicmusic.core.model.api.SongListItemDto

fun RankListPeakDto.toDomain(): RankListPeak {
    return RankListPeak(
        id = this.id,
        imageUrl = this.icon, // 映射 icon -> imageUrl
        title = this.title,
        updateFrequency = this.updateFrequency,
        items = this.top3.map { item ->
            RankTopItem(
                id = item.songId,
                title = item.title,
                artist = item.artistName
            )
        }
    )
}

fun RankListDetailDto.toDomain(): RankListDetail {
    return RankListDetail(
        id = this.id,
        imageUrl = this.icon,
        title = this.title,
        items = this.songs.map { it.toDomain() } // 复用 SongListItemDto 的转换
    )
}