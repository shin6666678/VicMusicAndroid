package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

/**
 * 评论的核心领域模型 (Domain Model)。
 * 代表一条独立的评论，无论是顶级评论还是回复。
 */
@Serializable
data class Comment(
    val id: String,
    val user: User, // 评论发布者信息
    val content: String, // 评论内容
    val createTime: Long, // 创建时间
    val likeCount: Int, // 点赞数
    val replyCount:Int,
    var isLiked: Boolean, // 当前用户是否已点赞
    val replyToUser: User? = null, // 如果是回复，回复的目标用户
    val parentId: String? = null, // 父评论ID (如果是二级评论)
    val rootId: String? = null // 根评论ID (如果是二级评论)
)

/**
 * 评论线程，用于在主评论列表中显示。
 * 它包含一个顶级评论（一级评论）以及该评论下的部分回复（二级评论）。
 * 这种结构非常适合在 LazyColumn 中直接渲染。
 */
@Serializable
data class CommentThread(
    val rootComment: Comment, // 顶级评论
    val replies: List<Comment>, // 该顶级评论下的二级回复列表 (通常只显示前几条)
    val totalReplyCount: Int, // 该顶级评论下的总回复数，用于显示“共 N 条回复”
    val hasMoreReplies: Boolean // 是否还有更多回复未显示
)

/**
 * 评论详情，用于评论详情页。
 * 它包含一个顶级评论以及其下的所有回复。
 */
@Serializable
data class CommentDetail(
    val rootComment: Comment, // 顶级评论
    val allReplies: List<Comment> // 该评论下的所有回复列表（平铺结构）
)
