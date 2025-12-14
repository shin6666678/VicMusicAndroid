package com.shin.vicmusic.feature.liked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.domain.Song // [修改] 引用 Domain Model
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource
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
                val response = datasource.getLikedSongs()
                if (response.status == 0 && response.data?.list != null) {
                    _uiState.value = LikedSongsUiState.Success(response.data.list)
                } else {
                    _uiState.value = LikedSongsUiState.Error(response.message ?: "获取失败")
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
            val response = datasource.likeSong(song.id)
            if (response.status == 0) {
                // 成功后，从本地列表中移除该歌曲 (UI即时刷新)
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
        }
    }
}

sealed class LikedSongsUiState {
    object Loading : LikedSongsUiState()
    data class Success(val songs: List<Song>) : LikedSongsUiState()
    data class Error(val message: String) : LikedSongsUiState()
}