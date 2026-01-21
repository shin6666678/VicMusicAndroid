package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.core.domain.CommentDetail
import com.shin.vicmusic.core.domain.CommentThread
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.api.CommentDto

/**
 * 将 CommentDto 转换为 Comment 领域模型。
 * 这个函数处理单个评论的转换。
 */
fun CommentDto.toComment(): Comment {
    val replyTo = if (parentId != null) {
        // 在实际应用中，这里可能需要从一个用户缓存中查找更完整的用户信息
        User(id = "", name = "", headImg = "") // Placeholder for replied-to user
    } else {
        null
    }

    return Comment(
        id = this.id,
        user = User(id = this.userId, name = this.userName, headImg = this.userHeadImg),
        content = this.content,
        createTime = this.createTime,
        likeCount = this.likeCount,
        isLiked = this.liked,
        replyToUser = replyTo,
        parentId = this.parentId,
        rootId = this.rootId
    )
}

/**
 * 将 CommentDto 列表转换为 CommentThread 列表。
 */
fun List<CommentDto>.toCommentThreads(): List<CommentThread> {
    val commentMap = this.associateBy { it.id }
    val rootComments = this.filter { it.parentId == null }

    return rootComments.map { rootDto ->
        val replies = rootDto.replyList?.mapNotNull { replyDto ->
            commentMap[replyDto.id]?.toComment() ?: replyDto.toComment()
        } ?: emptyList()

        CommentThread(
            rootComment = rootDto.toComment(),
            replies = replies,
            totalReplyCount = rootDto.replyList?.size ?: 0,
            hasMoreReplies = (rootDto.replyList?.size ?: 0) > replies.size
        )
    }
}

/**
 * 将一个 CommentDto (作为根评论) 转换为 CommentDetail 领域模型。
 * 这个函数假设传入的 rootDto (`this`) 包含了其下的所有回复 (`replyList`)。
 */
fun CommentDto.toCommentDetail(): CommentDetail {
    // 转换根评论 DTO
    val rootComment = this.toComment()

    // 转换所有的回复 DTOs
    val allReplies = this.replyList?.map { it.toComment() } ?: emptyList()

    return CommentDetail(
        rootComment = rootComment,
        allReplies = allReplies
    )
}
