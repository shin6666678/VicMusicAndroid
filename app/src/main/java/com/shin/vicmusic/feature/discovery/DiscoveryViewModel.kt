package com.shin.vicmusic.feature.discovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.model.request.SongPageReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    
    // 分页状态
    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private var isLoading = false


    init{
        loadData()
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
    
    fun refresh() {
        currentPage = 1
        isLastPage = false
        isLoading = false
        loadData()
    }

    // 处理喜欢/取消喜欢
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val result = likeRepository.toggleLike(song.id,1)
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