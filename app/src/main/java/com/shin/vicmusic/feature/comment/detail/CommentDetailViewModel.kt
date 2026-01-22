package com.shin.vicmusic.feature.comment.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommentRepository
import com.shin.vicmusic.core.domain.CommentDetail
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.CommentAddReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentDetailUiState(
    val commentDetail: CommentDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommentDetailViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCommentDetail(isRefresh = true)
    }

    fun loadCommentDetail(isRefresh: Boolean = false) {
        if (_uiState.value.isLoading && !isRefresh) return
        
        val commentId: String? = savedStateHandle["commentId"]
        if (commentId == null) {
            _uiState.value = CommentDetailUiState(error = "Comment ID not found")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = commentRepository.getCommentDetail(commentId)) {
                is Result.Success -> {
                    _uiState.value = CommentDetailUiState(commentDetail = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _uiState.value = CommentDetailUiState(error = result.message, isLoading = false)
                }
            }
        }
    }

    fun addComment(content: String, parentId: String?) {
        val resourceId: String? = savedStateHandle["resourceId"]
        val resourceType: String? = savedStateHandle["resourceType"]
        val rootCommentId: String? = savedStateHandle["commentId"]

        if (resourceId == null || resourceType == null || rootCommentId == null) {
            _uiState.value = _uiState.value.copy(error = "Missing required IDs to post comment.")
            return
        }

        viewModelScope.launch {
            val req = CommentAddReq(resourceType, resourceId, content, parentId)
            when (commentRepository.addComment(req)) {
                is Result.Success -> {
                    // Refresh the detail list after successfully adding a comment
                    loadCommentDetail(isRefresh = true)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = "评论失败，请重试")
                }
            }
        }
    }
}
