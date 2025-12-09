package com.shin.vicmusic.feature.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                // 调用之前在 MyRetrofitDatasource 添加的方法
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
}

sealed class LikedSongsUiState {
    object Loading : LikedSongsUiState()
    data class Success(val songs: List<Song>) : LikedSongsUiState()
    data class Error(val message: String) : LikedSongsUiState()
}