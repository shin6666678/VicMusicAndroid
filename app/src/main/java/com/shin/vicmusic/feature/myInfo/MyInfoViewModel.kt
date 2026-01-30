package com.shin.vicmusic.feature.myInfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class MyInfoUiState(
    val userInfo: UserInfo? = null,
    val userFeeds: List<Feed> = emptyList(),
    val isLoading: Boolean = false,
    val isFeedLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authManager: AuthManager,
    private val commonRepository: CommonRepository,
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val relationshipRepository: RelationshipRepository,
) : ViewModel() {

    private val targetUserId: String? = savedStateHandle[USER_ID_ARG]

    val currentUser = authManager.currentUser
    val isLoggedIn = authManager.isLoggedIn

    private val _uiState = MutableStateFlow(MyInfoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        if (targetUserId != null) {
            loadTargetUserInfo(targetUserId)
            loadUserFeeds(targetUserId)
        } else {
            // 个人主页：用户信息从 authManager 获取，但动态需要加载
            viewModelScope.launch {
                currentUser.collect { user ->
                    user?.id?.let { loadUserFeeds(it) }
                }
            }
        }
    }

    private fun loadTargetUserInfo(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = userRepository.getUserInfo(userId)
            when (result) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, userInfo = result.data) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    private fun loadUserFeeds(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFeedLoading = true) }
            val result = feedRepository.getUserFeeds(userId, 1, 20)
            when (result) {
                is Result.Success -> _uiState.update { it.copy(isFeedLoading = false, userFeeds = result.data.list ?: emptyList()) }
                is Result.Error -> _uiState.update { it.copy(isFeedLoading = false, error = result.message) }
            }
        }
    }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            val result = relationshipRepository.follow(userId, 0) // type 0 = User
            if (result is Result.Success) {
                // 刷新用户信息以更新关注状态
                if (targetUserId != null) {
                    loadTargetUserInfo(targetUserId)
                } else {
                    authManager.fetchUserInfo()
                }
            } else if (result is Result.Error) {
                _uiState.update { it.copy(error = result.message) }
            }
        }
    }

    fun refresh() {
        authManager.fetchUserInfo()
        loadData()
    }

    fun clearMessage() {
        _uiState.update { it.copy(error = null, message = null) }
    }

    // 独立修改背景图逻辑
    fun updateUserBg(localPath: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }

            try {
                val file = File(localPath)
                if (!file.exists()) {
                    throw Exception("文件不存在")
                }

                // 1. 上传图片
                val uploadResult = commonRepository.uploadImage(file, "user")
                val finalBgImgUrl = when (uploadResult) {
                    is Result.Success -> uploadResult.data
                    is Result.Error -> throw Exception(uploadResult.message ?: "图片上传失败")
                }

                // 2. 更新用户背景图 (单独接口)
                val updateResult = userRepository.updateUserBgImg(finalBgImgUrl)
                if (updateResult is Result.Error) {
                    throw Exception(updateResult.message ?: "更新背景失败")
                }

                // 3. 刷新用户信息
                authManager.fetchUserInfo()

                _uiState.update { it.copy(isLoading = false, message = "背景图更换成功") }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "操作失败") }
            }
        }
    }
}