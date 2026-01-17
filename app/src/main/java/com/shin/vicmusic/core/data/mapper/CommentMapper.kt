package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.core.model.api.CommentDto

/**
 * 将 CommentDto (数据层) 转换为 Comment (领域层)
 */
fun CommentDto.toDomain(): Comment {
    return Comment(
        id = this.id,
        userId = this.userId,
        userName = this.userName,
        userHeadImg = this.userHeadImg,
        resourceType = this.resourceType,
        resourceId = this.resourceId,
        content = this.content,
        likeCount = this.likeCount,
        liked = this.liked,
        createTime = this.createTime
    )
}
