package com.shin.vicmusic.feature.album.albumDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AlbumRepository
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.AlbumDetail
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.request.SongPageReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 专辑详情页的ViewModel
 * @param albumRepository 专辑数据仓库
 */
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val likeRepository: LikeRepository,
    savedStateHandle: SavedStateHandle // 获取传递的参数
) : ViewModel() {

    // 从路由参数中获取 albumId
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    // UI状态
    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAlbumDetail()
    }

    /**
     * 获取聚合详情数据 (专辑信息 + 歌曲列表)
     */
    fun loadAlbumDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 假设 Repository 返回的是 Result<AlbumDetail> (包含 album 和 songs)
            // 或者是你之前定义的 Domain Model
            val result = albumRepository.getAlbumDetail(AlbumDetailReq(id=albumId))

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> {
                        // [关键] 一次性解构数据
                        // 假设 result.data 是你定义的聚合 Domain 对象
                        val detail = result.data
                        state.copy(
                            isLoading = false,
                            album = detail.album, // 拿到专辑信息
                            songs = detail.songs.items, // 拿到歌曲列表 (如果是 PageResult 取 items)
                            error = null
                        )
                    }
                    is Result.Error -> state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }


    /**
     * 点赞/取消点赞 (优化了代码复用)
     */
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            // 1. 定义局部更新函数：只翻转指定 ID 的 isLiked 状态
            fun updateLikeState(targetId: String) {
                _uiState.update { state ->
                    state.copy(songs = state.songs.map { item ->
                        if (item.id == targetId) item.copy(isLiked = !item.isLiked) else item
                    })
                }
            }

            // 2. 乐观更新
            updateLikeState(song.id)

            // 3. 网络请求
            val result = likeRepository.likeSong(song.id)

            // 4. 如果失败，回滚
            if (result is Result.Error) {
                updateLikeState(song.id) // 再翻转一次就回去了
                // 可选：显示错误提示
                // _uiState.update { it.copy(error = result.message) }
            }
        }
    }
}

/**
 * 专辑详情页的UI状态
 * @param isLoading 是否正在加载
 * @param album 专辑信息
 * @param songs 专辑内的歌曲列表
 * @param error 错误信息
 */
data class AlbumDetailUiState(
    val isLoading: Boolean = false,
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)