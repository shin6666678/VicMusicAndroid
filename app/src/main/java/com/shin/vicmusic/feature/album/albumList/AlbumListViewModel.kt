package com.shin.vicmusic.feature.album.albumList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AlbumRepository
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.AlbumPageReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AlbumListUiState {
    object Loading : AlbumListUiState()
    data class Success(val albums: List<Album>) : AlbumListUiState()
    data class Error(val message: String) : AlbumListUiState()
}
@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    init {
        getAlbums()
    }

    private fun getAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumListUiState.Loading
            val result = albumRepository.getAlbums(AlbumPageReq())
            _uiState.value=when(result){
                is Result.Success-> {
                    AlbumListUiState.Success(result.data.list ?: emptyList())
                }
                is Result.Error -> {
                    AlbumListUiState.Error(result.message)
                }
            }
        }
    }
}

