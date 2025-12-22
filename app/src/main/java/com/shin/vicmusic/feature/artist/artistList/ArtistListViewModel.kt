package com.shin.vicmusic.feature.artist.artistList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistFilterState(
    val region: String = "全部",
    val type: String = "全部",
    val style: String = "全部"
)

@HiltViewModel
class ArtistListViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val userRepository: UserRepository
) : ViewModel(){
    // 直接存储从后端获取的列表，不再保存全量 originalArtists
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artist: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _filterState = MutableStateFlow(ArtistFilterState())
    val filterState: StateFlow<ArtistFilterState> = _filterState.asStateFlow()

    init {
        loadData()
    }

    // 根据当前筛选状态请求后端
    fun loadData() {
        viewModelScope.launch {
            val filter = _filterState.value
            // 发起网络请求
            val artistsResp = artistRepository.getArtists(
                region = filter.region,
                type = filter.type,
                style = filter.style
            )
            _artists.value = artistsResp.data?.list?: emptyList()
        }
    }

    fun updateRegion(region: String) {
        _filterState.value = _filterState.value.copy(region = region)
        loadData() // 状态更新后立即请求新数据
    }

    fun updateType(type: String) {
        _filterState.value = _filterState.value.copy(type = type)
        loadData()
    }

    fun updateStyle(style: String) {
        _filterState.value = _filterState.value.copy(style = style)
        loadData()
    }

    fun followArtist(artistId: String){
        viewModelScope.launch {
            val response=userRepository.follow(artistId, 1)
            if (response.status==0){
                // 更新 artist
                val artist=_artists.value.find { it.id==artistId }
                if (artist!=null){
                    _artists.value=_artists.value.map {
                        if (it.id==artistId) artist.copy(isFollowing=true) else it
                    }
                }
            }
        }
    }
}