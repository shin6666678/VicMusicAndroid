package com.shin.vicmusic.feature.feed.publish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.PublishFeedReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublishFeedUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPublished: Boolean = false,
    val sharedContent: Any? = null
)

@HiltViewModel
class PublishFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val songRepository: SongRepository, // 注入 SongRepository
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishFeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSharedContent()
    }

    private fun loadSharedContent() {
        val targetId: String? = savedStateHandle["targetId"]
        val targetType: String? = savedStateHandle["targetType"]

        if (targetId == null || targetType == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = when (targetType) {
                "song" -> songRepository.getSongDetail(targetId)
                // 未来可以扩展获取歌单、专辑的逻辑
                // "playlist" -> playlistRepository.getPlaylistDetail(targetId)
                // "album" -> albumRepository.getAlbumDetail(targetId)
                else -> Result.Error("不支持的分享类型")
            }

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, sharedContent = result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun publishFeed(comment: String) {
        val targetId: String? = savedStateHandle["targetId"]
        val targetType: String? = savedStateHandle["targetType"]

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val req = PublishFeedReq(
                targetId = targetId,
                targetType = targetType,
                comment = comment
            )
            when (val result = feedRepository.publishFeed(req)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isPublished = true)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
