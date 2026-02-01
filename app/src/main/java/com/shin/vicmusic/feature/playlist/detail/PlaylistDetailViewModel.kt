package com.shin.vicmusic.feature.playlist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.domain.MyNetWorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: PlaylistRepository,
    private val likeRepository: LikeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _detail = MutableStateFlow<PlaylistDetail?>(null)
    val detail = _detail.asStateFlow()
    private val playlistId: String = checkNotNull(savedStateHandle["id"])

    init {
        fetchDetail()
    }

    private fun fetchDetail() {
        viewModelScope.launch {
            when (val res = repository.getPlaylistDetail(playlistId)) {
                is MyNetWorkResult.Success -> {
                    _detail.value = res.data
                }
                is MyNetWorkResult.Error -> {
                }
            }
        }
    }

    fun changePublicStatus(id: String) {
        viewModelScope.launch {
            if (repository.changePublicStatus(id) is MyNetWorkResult.Success) {
                fetchDetail()
            }
        }
    }

    fun removeSongFromPlaylist(songId: String) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
            fetchDetail()
        }
    }

    fun toggleCollect() {
        val currentDetail = _detail.value ?: return
        val currentInfo = currentDetail.info

        viewModelScope.launch {
            // Type 3: Playlist
            when (val result = likeRepository.toggleLike(currentInfo.id, 3)) {
                is MyNetWorkResult.Success -> {
                    val newStatus = result.data
                    // ⚠️ 修正嵌套 Copy 逻辑错误：
                    // 我们必须更新 _detail (PlaylistDetail)，更新其内部的 info (PlayList)
                    _detail.update { oldDetail ->
                        oldDetail?.copy(
                            info = currentInfo.copy(isLiked = newStatus)
                        )
                    }
                }
                is MyNetWorkResult.Error -> {
                    println("Collect failed: ${result.message}")
                }
            }
        }
    }
}