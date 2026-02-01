package com.shin.vicmusic.feature.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val authManager: AuthManager, // ✅ 这里可以正常注入你的单例 Manager
    private val playlistRepository: PlaylistRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val isLoggedIn = authManager.isLoggedIn
    val currentUser = authManager.currentUser

    // 用于 UI 显示 Toast 提示
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()
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
                is MyNetWorkResult.Success -> {
                    _myPlaylists.value = result.data
                }
                is MyNetWorkResult.Error -> {
                    // 可以处理错误，比如提示用户
                }
            }
        }
    }
    // --- 新增：签到逻辑 ---
    fun checkIn() {
        viewModelScope.launch {
            // 1. 调用 Repository 执行签到请求
            when (val result = userRepository.checkIn()) {
                is MyNetWorkResult.Success -> {
                    _toastMessage.value = result.data // "签到成功..."

                    // 2. 关键步骤：签到成功后，通知 AuthManager 刷新用户信息
                    // 这样 UI 上的积分、经验条、等级才会立即更新
                    authManager.fetchUserInfo()
                }
                is MyNetWorkResult.Error -> {
                    _toastMessage.value = result.message
                }
                else -> {}
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}