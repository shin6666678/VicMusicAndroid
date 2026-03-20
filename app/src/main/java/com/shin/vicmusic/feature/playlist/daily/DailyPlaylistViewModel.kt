package com.shin.vicmusic.feature.playlist.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.manager.SongActionManager
import com.shin.vicmusic.core.model.UiState
import com.shin.vicmusic.core.model.error
import com.shin.vicmusic.core.model.loading
import com.shin.vicmusic.core.model.success
import com.shin.vicmusic.util.syncCustomStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyPlaylistUiState(
    val detail: PlaylistDetail
)

@HiltViewModel
class DailyPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val songActionManager: SongActionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(data = DailyPlaylistUiState(detail = PlaylistDetail.EMPTY))
    )
    val uiState = _uiState.asStateFlow()

    init {
        syncCustomStatus(_uiState, songActionManager) { state, updatedSong ->
            val currentDetail = state.data.detail
            val newSongs = currentDetail.songs.map { oldSong ->
                if (oldSong.id == updatedSong.id) updatedSong else oldSong
            }
            state.copy(
                data = state.data.copy(
                    detail = currentDetail.copy(songs = newSongs)
                )
            )
        }
        loadDailyPlaylist()
    }

    fun loadDailyPlaylist() {
        viewModelScope.launch {
            _uiState.update { it.loading() }
            when (val res = playlistRepository.getDailyPlaylist()) {
                is MyNetWorkResult.Success -> {
                    _uiState.update { it.success(DailyPlaylistUiState(res.data)) }
                }
                is MyNetWorkResult.Error -> {
                    _uiState.update { it.error(res.message) }
                }
            }
        }
    }
}
