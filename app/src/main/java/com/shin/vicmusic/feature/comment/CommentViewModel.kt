package com.shin.vicmusic.feature.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.common.AppConstants
import com.shin.vicmusic.core.data.repository.CommentRepository
import com.shin.vicmusic.core.domain.CommentThread
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.request.CommentAddReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentUiState(
    val comments: List<CommentThread> = emptyList(), // Changed to CommentThread
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val replyPages: Map<String, Int> = emptyMap() // rootCommentId -> nextPage
)

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentUiState())
    val uiState = _uiState.asStateFlow()

    // 加载评论列表
    fun loadComments(resourceType: String, resourceId: String, isRefresh: Boolean = false) {
        if (_uiState.value.isLoading || (!_uiState.value.hasMore && !isRefresh)) return

        viewModelScope.launch {
            val currentPage = if (isRefresh) 1 else _uiState.value.page
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = commentRepository.getComments(resourceType, resourceId, "all", currentPage, 20)) {
                is MyNetWorkResult.Success -> {
                    val newThreads = result.data.list ?: emptyList()
                    val pagination = result.data.pagination
                    val currentThreads = if (isRefresh) emptyList() else _uiState.value.comments

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        comments = currentThreads + newThreads,
                        page = pagination.page + 1,
                        hasMore = pagination.page < pagination.pages
                    )
                }
                is MyNetWorkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    // 添加评论
    fun addComment(resourceType: String, resourceId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            val req = CommentAddReq(resourceType, resourceId, content, parentId)
            when (commentRepository.addComment(req)) {
                is MyNetWorkResult.Success -> {
                    // 成功后刷新列表
                    loadComments(resourceType, resourceId, isRefresh = true)
                }
                is MyNetWorkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "评论失败，请重试")
                }
            }
        }
    }

    // 加载更多回复 (5条一页)
    fun loadMoreReplies(rootCommentId: String) {
        val currentPage = _uiState.value.replyPages[rootCommentId] ?: 1
        
        viewModelScope.launch {
            when (val result = commentRepository.getCommentDetail(rootCommentId, currentPage, 5)) {
                is MyNetWorkResult.Success -> {
                    val newReplies = result.data.allReplies
                    val updatedComments = _uiState.value.comments.map { thread ->
                        if (thread.rootComment.id == rootCommentId) {
                            val existingIds = thread.replies.map { it.id }.toSet()
                            val filteredNewReplies = newReplies.filter { it.id !in existingIds }
                            val totalReplies = thread.replies + filteredNewReplies
                            thread.copy(
                                replies = totalReplies,
                                hasMoreReplies = totalReplies.size < thread.totalReplyCount
                            )
                        } else {
                            thread
                        }
                    }
                    
                    val updatedReplyPages = _uiState.value.replyPages.toMutableMap()
                    updatedReplyPages[rootCommentId] = currentPage + 1
                    
                    _uiState.value = _uiState.value.copy(
                        comments = updatedComments,
                        replyPages = updatedReplyPages
                    )
                }
                is MyNetWorkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "加载回复失败: ${result.message}")
                }
            }
        }
    }

    // 点赞评论 (乐观更新)
    fun toggleLike(commentId: String, rootCommentId: String? = null) {
        val oldComments = _uiState.value.comments
        
        // 1. 立即在本地执行乐观更新
        val optimisticComments = oldComments.map { thread ->
            if (rootCommentId == null && thread.rootComment.id == commentId) {
                // Root comment
                val oldStatus = thread.rootComment.isLiked
                val newStatus = !oldStatus
                thread.copy(
                    rootComment = thread.rootComment.copy(
                        isLiked = newStatus,
                        likeCount = (if (newStatus) thread.rootComment.likeCount + 1 else thread.rootComment.likeCount - 1).coerceAtLeast(0)
                    )
                )
            } else if (rootCommentId != null && (thread.rootComment.id == rootCommentId || thread.replies.any { it.id == commentId })) {
                // Reply
                thread.copy(
                    replies = thread.replies.map { reply ->
                        if (reply.id == commentId) {
                            val oldStatus = reply.isLiked
                            val newStatus = !oldStatus
                            reply.copy(
                                isLiked = newStatus,
                                likeCount = (if (newStatus) reply.likeCount + 1 else reply.likeCount - 1).coerceAtLeast(0)
                            )
                        } else {
                            reply
                        }
                    }
                )
            } else {
                thread
            }
        }
        
        _uiState.value = _uiState.value.copy(comments = optimisticComments)

        // 2. 发送网络请求
        viewModelScope.launch {
            when (val result = commentRepository.likeComment(commentId)) {
                is MyNetWorkResult.Success -> {
                    // 请求成功，保持当前状态即可
                    // 如果服务器返回了最新的准确状态，也可以在这里做二次校验，
                    // 但为了体验丝滑，通常信任乐观更新，除非数据明显不一致。
                }
                is MyNetWorkResult.Error -> {
                    // 请求失败，回滚状态
                    _uiState.value = _uiState.value.copy(
                        comments = oldComments, 
                        error = "操作失败"
                    )
                }
            }
        }
    }
}
