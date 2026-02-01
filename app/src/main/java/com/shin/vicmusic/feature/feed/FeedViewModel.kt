package com.shin.vicmusic.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository // 新增
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.data.repository.UserRepository // 新增
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val commonRepository: CommonRepository, // 新增注入
    private val userRepository: UserRepository,     // 新增注入
    private val authManager: AuthManager
) : ViewModel() {

    // --- UI State ---
    private val _discoveryItems = MutableStateFlow<List<Feed>>(emptyList())
    val discoveryItems = _discoveryItems.asStateFlow()

    private val _followingItems = MutableStateFlow<List<Feed>>(emptyList())
    val followingItems = _followingItems.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    val currentUser = authManager.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // 新增：成功消息提示（可选）
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    init {
        loadFeeds()
    }

    fun updateTab(index: Int) {
        if (_selectedTabIndex.value == index) return
        _selectedTabIndex.value = index
        loadFeeds(isRefresh = true)
    }

    fun loadFeeds(isRefresh: Boolean = false) {
        if (_isLoading.value && !isRefresh) return

        viewModelScope.launch {
            // 注意：如果是后台静默刷新，不一定非要显示全屏loading，视需求而定
            if(!isRefresh) _isLoading.value = true
            _error.value = null

            val result = if (_selectedTabIndex.value == 0) {
                feedRepository.getFeeds(1, 20)
            } else {
                feedRepository.getFollowingFeeds(1, 20)
            }

            when (result) {
                is MyNetWorkResult.Success -> {
                    if (_selectedTabIndex.value == 0) {
                        _discoveryItems.value = result.data.list ?: emptyList()
                    } else {
                        _followingItems.value = result.data.list ?: emptyList()
                    }
                    _isLoading.value = false
                }
                is MyNetWorkResult.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearMessage() {
        _message.value = null
        _error.value = null
    }

    // 复用逻辑：更新背景图
    fun updateUserBg(localPath: String) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val file = File(localPath)
                if (!file.exists()) throw Exception("文件不存在")

                // 1. 上传图片
                val uploadResult = commonRepository.uploadImage(file, "user")
                val finalBgUrl = when(uploadResult) {
                    is MyNetWorkResult.Success -> uploadResult.data
                    is MyNetWorkResult.Error -> throw Exception(uploadResult.message)
                }

                // 2. 更新用户信息
                val updateResult = userRepository.updateUserBgImg(finalBgUrl)
                if (updateResult is MyNetWorkResult.Error) {
                    throw Exception(updateResult.message)
                }

                // 3. 刷新本地用户缓存 (这会自动触发 headerBackgroundImage 更新)
                authManager.fetchUserInfo()
                _message.value = "背景图更新成功"

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "更新失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
}