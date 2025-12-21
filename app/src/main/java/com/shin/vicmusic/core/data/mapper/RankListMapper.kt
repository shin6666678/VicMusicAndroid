package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.model.api.RankListPeakDto
import com.shin.vicmusic.core.model.api.SongListItemDto

fun RankListPeakDto.toDomain(): RankListPeak {
    return RankListPeak(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        items = this.items.map { item ->
            // 检查元素是否为 SongListItemDto
            if (item is SongListItemDto) {
                item.toDomain() // 调用你之前定义的 Song 转换函数
            } else {
                item // 其他类型保持原样
            }
        }
    )
}