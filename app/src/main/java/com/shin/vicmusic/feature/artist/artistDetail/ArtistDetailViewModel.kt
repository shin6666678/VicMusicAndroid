package com.shin.vicmusic.feature.artist.artistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.SongPageReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val songRepository: SongRepository,
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
            when(val artistResult = artistRepository.getArtistDetailById(artistId)){
                is Result.Success->{
                    _artist.value = artistResult.data
                    val songsResult = songRepository.getSongs(SongPageReq(artistId=artistId))
                    if (songsResult is Result.Success) {
                        _songs.value = songsResult.data.list?:emptyList()
                    }
                }
                is Result.Error ->{}
            }
        }
    }
}