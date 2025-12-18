package com.shin.vicmusic.feature.discovery.musicHall.artistList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistListViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel(){
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artist: StateFlow<List<Artist>> = _artists

    init {
        loadData()
    }
    fun loadData() {
        viewModelScope.launch {
            val artistsResp = artistRepository.getArtists()
            _artists.value = artistsResp.data?:emptyList()
        }
    }
}