package com.shin.vicmusic.feature.album.albumDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AlbumRepository
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val likeRepository: LikeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAlbumDetail()
    }

    fun loadAlbumDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = albumRepository.getAlbumDetail(AlbumDetailReq(id = albumId))) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            album = result.data.album,
                            songs = result.data.songs.items,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun toggleLike() {
        val currentAlbum = _uiState.value.album ?: return

        viewModelScope.launch {
            when (val response = likeRepository.toggleLike(currentAlbum.id, 2)) {
                is Result.Success -> {
                    val newStatus = response.data
                    _uiState.update { state ->
                        state.copy(
                            album = currentAlbum.copy(isLiked = newStatus)
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = response.message) }
                }
            }
        }
    }
}

data class AlbumDetailUiState(
    val isLoading: Boolean = false,
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)