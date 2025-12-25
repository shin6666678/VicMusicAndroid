package com.shin.vicmusic.feature.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val authManager: AuthManager, // ✅ 这里可以正常注入你的单例 Manager
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    // 直接透传 Manager 的状态给 UI 使用
    val isLoggedIn = authManager.isLoggedIn
    val currentUser = authManager.currentUser
    //  暴露获取用户信息的方法
    fun fetchUserInfo() {
        authManager.fetchUserInfo()
    }

    // 歌单列表状态
    private val _myPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val myPlaylists = _myPlaylists.asStateFlow()
    //获取我的歌单
    fun fetchMyPlaylists() {
        viewModelScope.launch {
            // 这里假设 Repository 会处理 token 等逻辑，或者 API 不需要特殊处理
            when (val result = playlistRepository.getMyPlaylists()) {
                is Result.Success -> {
                    _myPlaylists.value = result.data
                }
                is Result.Error -> {
                    // 可以处理错误，比如提示用户
                }
            }
        }
    }
}