package com.shin.vicmusic.feature.playlist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.SongActionManager
import com.shin.vicmusic.core.model.UiState
import com.shin.vicmusic.core.model.error
import com.shin.vicmusic.core.model.loading
import com.shin.vicmusic.core.model.success
import com.shin.vicmusic.feature.myInfo.edit.MyInfoEditUiState
import com.shin.vicmusic.util.syncCustomStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class PlayListDetailUiState(
    val detail: PlaylistDetail
)
@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: PlaylistRepository,
    private val likeRepository: LikeRepository,
    private val commonRepository: CommonRepository,
    private val songActionManager: SongActionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(
        UiState(data = PlayListDetailUiState(detail = PlaylistDetail.EMPTY))
    )
    val uiState = _uiState.asStateFlow()

    // 缓存原始数据，用于 saveChanges 时的差分对比
    private var originalData: PlaylistDetail? = null

    init {
        syncCustomStatus(_uiState, songActionManager) { state, updatedSong ->
            // 如果 state 还是 loading 状态或者没有 data，直接返回
            val currentDetail = state.data.detail

            // 找到并更新歌曲列表
            val newSongs = currentDetail.songs.map { oldSong ->
                if (oldSong.id == updatedSong.id) updatedSong else oldSong
            }

            // 层层 copy 回去
            state.copy(
                data = state.data.copy(
                    detail = currentDetail.copy(songs = newSongs)
                )
            )
        }
        fetchDetail()
    }

    fun fetchDetail() {
        viewModelScope.launch {
            _uiState.update { it.loading() }
            when (val res = repository.getPlaylistDetail(playlistId)) {
                is MyNetWorkResult.Success -> {
                    val detail = res.data
                    originalData = detail
                    _uiState.update { it.success(PlayListDetailUiState(detail)) }
                }
                is MyNetWorkResult.Error -> {
                    _uiState.update { it.error(res.message) }
                }
            }
        }
    }

    // --- UI 事件处理 ---

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(
                data = state.data.copy(
                    detail = state.data.detail.copy(
                        info = state.data.detail.info.copy(name = newName)
                    )
                )
            )
        }
    }

    fun removeSongFromPlaylist(songId: String) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
            fetchDetail()
        }
    }
    fun onDescriptionChange(newDesc: String) {
        _uiState.update { state ->
            state.copy(
                data = state.data.copy(
                    detail = state.data.detail.copy(
                        info = state.data.detail.info.copy(description = newDesc)
                    )
                )
            )
        }
    }


    fun onPublicStatusChange(isPublic: Int) {
        _uiState.update { state ->
            state.copy(
                data = state.data.copy(
                    detail = state.data.detail.copy(
                        info = state.data.detail.info.copy(isPublic = isPublic)
                    )
                )
            )
        }
        saveChanges()
    }

    fun onNewCoverSelected(localPath: String) {
        _uiState.update {
            it.copy(
                data = it.data.copy(
                    detail = it.data.detail.copy(
                        info = it.data.detail.info.copy(cover = localPath)
                    )
                )
            )
        }
    }

    fun toggleCollect() {
        viewModelScope.launch {
            // Type 3 代表歌单
            when (val result = likeRepository.toggleLike(playlistId, 3)) {
                is MyNetWorkResult.Success -> {
                    val newStatus = result.data
                    _uiState.update { state ->
                        state.copy(
                            data = state.data.copy(
                                detail = state.data.detail.copy(
                                    info = state.data.detail.info.copy(isLiked = newStatus)
                                )
                            )
                        )
                    }
                }
                is MyNetWorkResult.Error -> {
                    _uiState.update { it.error(result.message) }
                }
            }
        }
    }

    fun saveChanges() {
        val currentDetail = _uiState.value.data.detail
        val original = originalData ?: return

        if (currentDetail.info.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "歌单名称不能为空(Name cannot be empty)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.loading() }
            try {
                // 1. 处理封面上传
                var finalCoverUrl: String? = null
                if (currentDetail.info.cover != original.info.cover && !currentDetail.info.cover.startsWith("http")) {
                    val file = File(currentDetail.info.cover)
                    if (file.exists()) {
                        val uploadRes = commonRepository.uploadImage(file, "playlist")
                        if (uploadRes is MyNetWorkResult.Success) {
                            finalCoverUrl = uploadRes.data
                        } else {
                            throw Exception("封面上传失败(Cover upload failed)")
                        }
                    }
                }

                // 2. 差分更新判断
                val nameUpdate = if (currentDetail.info.name != original.info.name) currentDetail.info.name else null
                val descUpdate = if (currentDetail.info.description != original.info.description) currentDetail.info.description else null
                val publicUpdate = if (currentDetail.info.isPublic != original.info.isPublic) currentDetail.info.isPublic else null

                // 3. 调用 Repository 更新
                val result = repository.updatePlaylist(
                    id = playlistId,
                    name = nameUpdate,
                    description = descUpdate,
                    cover = finalCoverUrl,
                    isPublic = publicUpdate
                )

                if (result is MyNetWorkResult.Success) {
                    fetchDetail() // 刷新成功后重置 originalData
                } else {
                    throw Exception((result as MyNetWorkResult.Error).message)
                }

            } catch (e: Exception) {
                _uiState.update { it.error(e.message ?: "保存失败(Save failed)") }
            }
        }
    }
}