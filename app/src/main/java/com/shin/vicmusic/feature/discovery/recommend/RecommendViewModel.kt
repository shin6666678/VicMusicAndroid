package com.shin.vicmusic.feature.discovery.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.model.api.SongListItemDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val songRepository: SongRepository,
    authManager: AuthManager
) : ViewModel() {

    val userInfo: StateFlow<UserInfo?> = authManager.currentUser

    private val _recommendCard = MutableStateFlow(RecommendCard(title = "", songs = emptyList()))
    val recommendCard: StateFlow<RecommendCard> = _recommendCard.asStateFlow()


    private val _dailyRecommendSongs = MutableStateFlow<List<SongListItemDto>>(emptyList())
    val dailyRecommendSongs = _dailyRecommendSongs.asStateFlow()


    init {
        loadData()
    }
    private fun loadData() {
        viewModelScope.launch {
            fetchDailyRecommendSongs()
            fetchAlsoListening()
        }
    }

    private fun fetchDailyRecommendSongs() {
        viewModelScope.launch {
            when(val result=songRepository.getDailyRecommendSongs()){
                is MyNetWorkResult.Success->{
                    _dailyRecommendSongs.value = result.data
                }
                is MyNetWorkResult.Error->{}
            }
        }
    }
    private fun fetchAlsoListening() {
        viewModelScope.launch {
            val result = songRepository.getAlsoListening()
            if (result is MyNetWorkResult.Success) {
                _recommendCard.value = result.data
            }
        }
    }
}