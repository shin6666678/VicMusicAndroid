package com.shin.vicmusic.feature.song

import androidx.lifecycle.SavedStateHandle // 导入 SavedStateHandle 用于获取导航参数
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 数据状态密封类，用于表示UI加载状态
sealed class SongUiState {
    object Loading : SongUiState()
    data class Success(val song: Song) : SongUiState()
    data class Error(val message: String) : SongUiState()
}

/**
 * 歌曲详情页的 ViewModel。
 * 职责：根据 songId 加载歌曲数据，并命令 PlayerViewModel 播放歌曲。
 * 它不再自己管理 ExoPlayer 实例或播放状态。
 */
@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // 用于获取导航参数
    private val datasource: MyRetrofitDatasource
) : ViewModel() {

    // 从导航参数中获取 songId
    val songId: String? = savedStateHandle["songId"]

    // UI 状态，用于驱动界面显示加载中、成功或失败
    private val _songUiState = MutableStateFlow<SongUiState>(SongUiState.Loading)
    val songUiState: StateFlow<SongUiState> = _songUiState.asStateFlow()

    init {
        // ViewModel 初始化时，检查 songId 是否存在
        if (songId.isNullOrBlank()) {
            // 如果 songId 不存在，更新UI状态为错误
            _songUiState.value = SongUiState.Error("歌曲ID不存在")
        } else {
            // 如果 songId 存在，开始加载歌曲数据
            loadSongDetail(songId)
        }
    }

    /**
     * 加载歌曲数据，并在成功后命令 PlayerViewModel 播放。
     * @param id 歌曲ID
     */
    fun loadSongDetail(id: String) {
        viewModelScope.launch {
            // 1. 设置UI状态为加载中
            _songUiState.value = SongUiState.Loading
            try {
                // 2. 发起网络请求获取歌曲详情
                val response = datasource.songDetail(id)
                response.data?.let {
                    // 3. 网络请求成功且数据不为空
                    _songUiState.value = SongUiState.Success(it) // 更新UI状态为成功 // 命令全局 PlayerViewModel 播放这首歌
                } ?: run {
                    // 4. 网络请求成功但数据为空
                    _songUiState.value = SongUiState.Error("未获取到歌曲数据")
                }
            } catch (e: Exception) {
                // 5. 网络请求或处理过程中发生异常
                _songUiState.value = SongUiState.Error("加载失败：${e.message ?: "未知错误"}")
            }
        }
    }

    /**
     * [新增] 切换喜欢状态
     */
    fun toggleLike() {
        val currentState = _songUiState.value
        if (currentState is SongUiState.Success) {
            val currentSong = currentState.song

            viewModelScope.launch {
                // 发送网络请求
                val response = datasource.likeSong(currentSong.id)

                if (response.status == 0) {
                    // 请求成功，更新本地 UI 状态 (翻转 isLiked)
                    _songUiState.update {
                        if (it is SongUiState.Success) {
                            it.copy(song = it.song.copy(isLiked = !it.song.isLiked))
                        } else {
                            it
                        }
                    }
                } else {
                    // 可以选择处理错误，例如显示 Toast
                }
            }
        }
    }

}