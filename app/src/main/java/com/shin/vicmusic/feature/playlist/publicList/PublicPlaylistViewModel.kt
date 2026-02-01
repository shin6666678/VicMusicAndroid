package com.shin.vicmusic.feature.playlist.publicList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicPlaylistViewModel @Inject constructor(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    fun fetchPublicPlaylists() {
        viewModelScope.launch {
            val resp=repository.getPublicPlaylists()
            if (resp is MyNetWorkResult.Success) {
                _playlists.value = resp.data.list?:emptyList()
            }
        }
    }
}