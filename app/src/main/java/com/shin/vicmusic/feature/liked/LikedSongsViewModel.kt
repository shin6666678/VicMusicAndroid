package com.shin.vicmusic.feature.liked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
    private val likeRepository: LikeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LikedSongsUiState>(LikedSongsUiState.Loading)
    val uiState: StateFlow<LikedSongsUiState> = _uiState.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _uiEvent = Channel<String>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var fetchJob: Job? = null

    init {
        switchTab(0)
    }

    fun switchTab(index: Int) {
        _tabIndex.value = index
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            loadDataForTab(index)
        }
    }

    private suspend fun loadDataForTab(index: Int) {
        val currentState = _uiState.value
        // 智能 Loading：如果当前已经是 Success 状态（意味着用户看到了旧数据），
        // 切换 Tab 时就不显示全屏 Loading，而是静默刷新。
        val shouldShowLoading = currentState !is LikedSongsUiState.Success

        if (shouldShowLoading) {
            _uiState.value = LikedSongsUiState.Loading
        }

        when (index) {
            0 -> fetchLikedSongs()
            1 -> fetchLikedAlbums()
            2 -> fetchLikedPlaylists()
        }
    }

    private suspend fun fetchLikedSongs() {
        handleResult(likeRepository.likedSongs()) { currentData, newSongs ->
            currentData.copy(songs = newSongs.list ?: emptyList())
        }
    }

    private suspend fun fetchLikedAlbums() {
        handleResult(likeRepository.likedAlbums()) { currentData, newAlbums ->
            currentData.copy(albums = newAlbums.list ?: emptyList())
        }
    }

    private suspend fun fetchLikedPlaylists() {
        handleResult(likeRepository.likedPlaylists()) { currentData, newPlaylists ->
            currentData.copy(playlists = newPlaylists.list ?: emptyList())
        }
    }

    private fun <T> handleResult(
        myNetWorkResult: MyNetWorkResult<T>,
        reducer: (LikedSongsUiState.Success, T) -> LikedSongsUiState.Success
    ) {
        when (myNetWorkResult) {
            is MyNetWorkResult.Success -> {
                _uiState.update { currentState ->
                    val baseState = (currentState as? LikedSongsUiState.Success)
                        ?: LikedSongsUiState.Success()
                    reducer(baseState, myNetWorkResult.data)
                }
            }
            is MyNetWorkResult.Error -> {
                // 1. 如果当前已经是 Success（用户看着旧数据），不要把页面变成 Error，而是弹窗提示。
                // 2. 如果当前是 Loading（用户看着转圈），则显示 Error 页面让用户重试。
                if (_uiState.value is LikedSongsUiState.Success) {
                    sendEvent(myNetWorkResult.message)
                } else {
                    _uiState.value = LikedSongsUiState.Error(myNetWorkResult.message)
                }
            }
        }
    }

    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val result = likeRepository.toggleLike(song.id, 1)
            when (result) {
                is MyNetWorkResult.Success -> {
                    _uiState.update { state ->
                        if (state is LikedSongsUiState.Success) {
                            state.copy(songs = state.songs.filter { it.id != song.id })
                        } else {
                            state
                        }
                    }
                }
                is MyNetWorkResult.Error -> {
                    // 操作失败，通知 UI 弹窗，不影响当前列表显示
                    sendEvent("取消收藏失败: ${result.message}")
                }
            }
        }
    }

    private fun sendEvent(message: String) {
        viewModelScope.launch {
            _uiEvent.send(message)
        }
    }
}

sealed class LikedSongsUiState {
    object Loading : LikedSongsUiState()
    data class Success(
        val songs: List<Song> = emptyList(),
        val albums: List<Album> = emptyList(),
        val playlists: List<Playlist> = emptyList()
    ) : LikedSongsUiState()
    data class Error(val message: String) : LikedSongsUiState()
}