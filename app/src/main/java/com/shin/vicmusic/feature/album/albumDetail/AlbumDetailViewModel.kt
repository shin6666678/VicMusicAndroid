package com.shin.vicmusic.feature.album.albumDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AlbumRepository
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Song
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
    private val songRepository: SongRepository,
    private val likeRepository: LikeRepository,
    savedStateHandle: SavedStateHandle // 获取传递的参数
) : ViewModel() {

    // 从路由参数中获取 albumId
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    // UI状态
    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getAlbumDetail(albumId)
        getSongsByAlbumId(albumId)
    }

    /**
     * 获取专辑详情
     * @param albumId 专辑ID
     */
    fun getAlbumDetail(albumId: String) {
        viewModelScope.launch {
            // 设置为加载中状态
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 调用repository获取数据
                val response = albumRepository.getAlbumDetail(albumId)
                if (response.status == 0 && response.data != null) {
                    // 更新为成功状态
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            album = response.data,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message ?: "获取专辑详情失败"
                        )
                    }
                }
            } catch (e: Exception) {
                // 更新为失败状态
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun getSongsByAlbumId(albumId: String) {
        viewModelScope.launch {
            try {
                val response = songRepository.getSongs(
                    SongPageReq(
                        albumId = albumId
                    )
                )
                if (response.status == 0 && response.data != null) {
                    _uiState.update {
                        it.copy(songs = response.data.list ?: emptyList())
                    }
                }
            } catch (e: Exception) {
                // Ignore for now, or handle error separately
            }
        }
    }

    fun toggleLike(song: Song) {
        viewModelScope.launch {
            // 乐观更新 UI
            _uiState.update { currentState ->
                val updatedSongs = currentState.songs.map {
                    if (it.id == song.id) it.copy(isLiked = !it.isLiked) else it
                }
                currentState.copy(songs = updatedSongs)
            }

            try {
                val response = likeRepository.likeSong(song.id)
                if (response.status != 0) {
                    // 如果失败，回滚状态
                    _uiState.update { currentState ->
                        val updatedSongs = currentState.songs.map {
                            if (it.id == song.id) it.copy(isLiked = !it.isLiked) else it
                        }
                        currentState.copy(songs = updatedSongs)
                    }
                }
            } catch (e: Exception) {
                // 异常处理，回滚状态
                _uiState.update { currentState ->
                    val updatedSongs = currentState.songs.map {
                        if (it.id == song.id) it.copy(isLiked = !it.isLiked) else it
                    }
                    currentState.copy(songs = updatedSongs)
                }
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