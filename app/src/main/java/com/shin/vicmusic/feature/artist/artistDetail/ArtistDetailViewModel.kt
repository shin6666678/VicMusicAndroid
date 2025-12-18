package com.shin.vicmusic.feature.artist.artistDetail

import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.domain.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    artistRepository: ArtistRepository
){
    private val _artist: Artist=artistRepository.getArtistById()
    val artist: Artist=_artist
}