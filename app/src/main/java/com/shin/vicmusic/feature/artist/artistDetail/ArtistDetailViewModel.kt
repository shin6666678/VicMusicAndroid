package com.shin.vicmusic.feature.artist.artistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.ArtistDetailReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchArtistDetail()
    }

    private fun fetchArtistDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result=artistRepository.getArtistDetail(ArtistDetailReq(id = artistId))
            _uiState.update { state ->
                when (result) {
                    is MyNetWorkResult.Success -> {
                        val detail = result.data
                        state.copy(
                            isLoading = false,
                            artist = detail.artist,
                            songs = detail.songs.items, // 拿到歌曲列表 (如果是 PageResult 取 items)
                            error = null
                        )
                    }
                    is MyNetWorkResult.Error -> state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}

data class ArtistDetailUiState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)