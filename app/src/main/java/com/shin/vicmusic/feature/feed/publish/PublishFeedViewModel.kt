package com.shin.vicmusic.feature.feed.publish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.PublishFeedReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublishFeedUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPublished: Boolean = false
)

@HiltViewModel
class PublishFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishFeedUiState())
    val uiState = _uiState.asStateFlow()

    fun publishFeed(comment: String) {
        val targetId: String? = savedStateHandle["targetId"]
        val targetType: String? = savedStateHandle["targetType"]

        viewModelScope.launch {
            _uiState.value = PublishFeedUiState(isLoading = true)
            val req = PublishFeedReq(
                targetId = targetId,
                targetType = targetType,
                comment = comment
            )
            when (val result = feedRepository.publishFeed(req)) {
                is Result.Success -> {
                    _uiState.value = PublishFeedUiState(isPublished = true)
                }
                is Result.Error -> {
                    _uiState.value = PublishFeedUiState(error = result.message)
                }
            }
        }
    }
}
