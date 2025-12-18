package com.shin.vicmusic.feature.artist.artistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])

    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    init {
        fetchArtistDetail()
    }

    private fun fetchArtistDetail() {
        viewModelScope.launch {
            val response = artistRepository.getArtistDetailById(artistId)
            if (response.status == 0) {
                _artist.value = response.data
            }
        }
    }
}