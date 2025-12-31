package com.shin.vicmusic.feature.discovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.request.SongPageReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val authManager: AuthManager,
    private val likeRepository: LikeRepository,
) : ViewModel(){
    private val _datum = MutableStateFlow<List<Song>>(emptyList())
    val datum: StateFlow<List<Song>> = _datum

    private val _dailyRecommendSongs = MutableStateFlow<List<SongListItemDto>>(emptyList())
    val dailyRecommendSongs = _dailyRecommendSongs.asStateFlow()

    // 新增状态
    private val _alsoListeningCard = MutableStateFlow<RecommendCard>( RecommendCard("", emptyList()))
    val alsoListeningCard = _alsoListeningCard.asStateFlow()
    
    // 分页状态
    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private var isLoading = false

    //直接链接到 AuthViewModel 的 currentUser，实现由于单一数据源(Single Source of Truth)
    // 这样当 AuthViewModel 登录/登出或更新用户信息时，这里会自动同步
    val user: StateFlow<UserInfo?> = authManager.currentUser

    init{
        loadData()
       // fetchDailyRecommendSongs()
        fetchAlsoListening()
    }

    fun loadData() {
        if (isLoading || isLastPage) return
        isLoading = true
        
        viewModelScope.launch {
            try {
                val result = songRepository.getSongs(SongPageReq(page = currentPage, size = pageSize))
                when(result){
                    is Result.Success->{
                        val newSongs = result.data.list ?: emptyList()
                        if (newSongs.size < pageSize) {
                            isLastPage = true
                        }
                        if (currentPage == 1) {
                            _datum.value = newSongs
                        } else {
                            _datum.update { it + newSongs }
                        }

                        if (newSongs.isNotEmpty()) {
                            currentPage++
                        }
                    }
                     is Result.Error->{}
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
            } finally {
                isLoading = false
            }
        }
    }
    private fun fetchDailyRecommendSongs() {
        viewModelScope.launch {
            when(val result=songRepository.getDailyRecommendSongs()){
                is Result.Success->{
                    _dailyRecommendSongs.value = result.data
                }
                is Result.Error->{}
            }
        }
    }
    private fun fetchAlsoListening() {
        viewModelScope.launch {
            val result = songRepository.getAlsoListening()
            if (result is Result.Success) {
                _alsoListeningCard.value = result.data
            }
        }
    }
    
    fun refresh() {
        currentPage = 1
        isLastPage = false
        isLoading = false
        loadData()
    }

    // 处理喜欢/取消喜欢
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val result = likeRepository.likeSong(song.id)
            when(result){
                is Result.Success->{
                    _datum.update { list ->
                        list.map { item ->
                            if (item.id == song.id) item.copy(isLiked = !item.isLiked) else item
                        }
                    }
                }
                is Result.Error->{}
            }
        }
    }

    companion object{
        const val TAG ="DiscoveryViewModel"
    }
}