package com.shin.vicmusic.feature.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommentRepository
import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.CommentAddReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentUiState(
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
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
                is Result.Success -> {
                    val newComments = result.data.list ?: emptyList()
                    val pagination = result.data.pagination
                    val currentComments = if (isRefresh) emptyList() else _uiState.value.comments

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        comments = currentComments + newComments,
                        page = pagination.page + 1,
                        hasMore = pagination.page < pagination.pages
                    )
                }
                is Result.Error -> {
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
                is Result.Success -> {
                    // 成功后刷新列表
                    loadComments(resourceType, resourceId, isRefresh = true)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = "评论失败，请重试")
                }
            }
        }
    }

    // 点赞评论
    fun toggleLike(commentId: String) {
        viewModelScope.launch {
            when (val result = commentRepository.likeComment(commentId)) {
                is Result.Success -> {
                    // 更新 UI 状态
                    val newStatus = result.data == 1 // 1 是点赞, 0 是取消
                    _uiState.value = _uiState.value.copy(
                        comments = _uiState.value.comments.map { comment ->
                            if (comment.id == commentId) {
                                comment.copy(
                                    liked = newStatus,
                                    likeCount = if (newStatus) comment.likeCount + 1 else comment.likeCount - 1
                                )
                            } else {
                                comment
                            }
                        }
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = "操作失败")
                }
            }
        }
    }
}
