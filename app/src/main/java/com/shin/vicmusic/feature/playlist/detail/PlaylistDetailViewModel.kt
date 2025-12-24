package com.shin.vicmusic.feature.playlist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: PlaylistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _detail = MutableStateFlow<PlaylistDetail?>(null)
    val detail = _detail.asStateFlow()

    // 从路由参数获取ID
    private val playlistId: String = checkNotNull(savedStateHandle["id"])

    init {
        fetchDetail()
    }

    private fun fetchDetail() {
        viewModelScope.launch {
            val res = repository.getPlaylistDetail(playlistId)
            if (res is Result.Success) {
                _detail.value = res.data
            }
        }
    }
}