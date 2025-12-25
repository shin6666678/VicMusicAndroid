package com.shin.vicmusic.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    fun fetchMyPlaylists() {
        viewModelScope.launch {
            if (repository.getMyPlaylists() is Result.Success) {
                _playlists.value = (repository.getMyPlaylists() as Result.Success).data
            }
        }
    }

    fun createPlaylist(name: String, description: String? = null, cover: File? = null) {
        viewModelScope.launch {
            val result = repository.addPlaylist(name, description, cover)
            if (result is Result.Success) {
                fetchMyPlaylists() // Refresh list
            }
        }
    }

    fun updatePlaylist(id: String, name: String, description: String? = null, cover: File? = null) {
        viewModelScope.launch {
            val result = repository.updatePlaylist(id, name, description, cover)
            if (result is Result.Success) {
                fetchMyPlaylists() // Refresh list
            }
        }
    }
}