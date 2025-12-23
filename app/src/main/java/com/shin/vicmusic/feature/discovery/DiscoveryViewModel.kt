package com.shin.vicmusic.feature.discovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.User
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

    //直接链接到 AuthViewModel 的 currentUser，实现由于单一数据源(Single Source of Truth)
    // 这样当 AuthViewModel 登录/登出或更新用户信息时，这里会自动同步
    val user: StateFlow<User?> = authManager.currentUser

    init{
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val songs= songRepository.getSongs(SongPageReq())
            Log.d(TAG, "Fetched songs: ${songs.data?.list?.size}")
            _datum.value=songs.data?.list?:emptyList()
        }
    }

    // 处理喜欢/取消喜欢
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val response = likeRepository.likeSong(song.id)
            if (response.status == 0) {
                _datum.update { list ->
                    list.map { item ->
                        if (item.id == song.id) item.copy(isLiked = !item.isLiked) else item
                    }
                }
            }
        }
    }

    companion object{
        const val TAG ="DiscoveryViewModel"
    }
}