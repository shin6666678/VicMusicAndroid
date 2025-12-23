package com.shin.vicmusic.feature.liked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LikedSongsUiState>(LikedSongsUiState.Loading)
    val uiState: StateFlow<LikedSongsUiState> = _uiState.asStateFlow()

    init {
        fetchLikedSongs()
    }

    fun fetchLikedSongs() {
        viewModelScope.launch {
            _uiState.value = LikedSongsUiState.Loading
            try {
                val result = likeRepository.likedSongs()
                when(result){
                    is Result.Success -> {
                        val songs = result.data.list
                        _uiState.value = LikedSongsUiState.Success(result.data.list?: emptyList())
                    }
                    is Result.Error -> {
                        _uiState.value = LikedSongsUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LikedSongsUiState.Error(e.message ?: "未知错误")
            }
        }
    }
    //  在喜欢列表中取消喜欢
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            // 调用后端接口
            val result = likeRepository.likeSong(song.id)
            when(result){
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is LikedSongsUiState.Success) {
                            // 过滤掉已被取消喜欢的歌曲
                            val newList = state.songs.filter { it.id != song.id }
                            LikedSongsUiState.Success(newList)
                        } else {
                            state
                        }
                    }
                }
                is Result.Error -> {
                    val message = result.message
                }
            }
        }
    }
}

sealed class LikedSongsUiState {
    object Loading : LikedSongsUiState()
    data class Success(val songs: List<Song>) : LikedSongsUiState()
    data class Error(val message: String) : LikedSongsUiState()
}