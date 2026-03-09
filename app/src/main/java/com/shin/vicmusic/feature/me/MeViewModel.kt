package com.shin.vicmusic.feature.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.HistoryRepository
import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.data.repository.PlaylistRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.RecentPlayCount
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val playlistRepository: PlaylistRepository,
    private val historyRepository: HistoryRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    data class MeUiState(
        val myPlaylists: List<Playlist> = emptyList(),
        val likedPlayLists : List<Playlist> = emptyList(),
        val recentPlay: RecentPlayCount = RecentPlayCount(0, ""),
        val isLoading: Boolean = false
    )
    private val _uiState = MutableStateFlow(MeUiState())
    val uiState = _uiState.asStateFlow()
    val isLoggedIn = authManager.isLoggedIn
    val currentUser = authManager.currentUser
    init {
        // 当登录状态变为 true 时，自动拉取数据
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn == true) {
                    refreshAllData()
                }
            }
        }
    }
    fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 并发执行三个请求，提高加载效率
            val playlistsDeferred = async { playlistRepository.getMyPlaylists() }
            val countDeferred = async { historyRepository.getRecentPlayCount() }
            val likedPlayListsDeferred = async { likeRepository.likedPlaylists() }

            val playlistRes = playlistsDeferred.await()
            val countRes = countDeferred.await()
            val likedPlayListRes =likedPlayListsDeferred.await()

            _uiState.update { state ->
                state.copy(
                    myPlaylists = (playlistRes as? MyNetWorkResult.Success)?.data ?: emptyList(),
                    likedPlayLists = (likedPlayListRes as? MyNetWorkResult.Success)?.data?.list ?: emptyList(),
                    recentPlay = (countRes as? MyNetWorkResult.Success)?.data ?: RecentPlayCount(0, ""),
                    isLoading = false
                )
            }
            // 同时刷新用户信息
            authManager.fetchUserInfo()
        }
    }

    // 用于 UI 显示 Toast 提示
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()
    //  暴露获取用户信息的方法
    fun fetchUserInfo() {
        authManager.fetchUserInfo()
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