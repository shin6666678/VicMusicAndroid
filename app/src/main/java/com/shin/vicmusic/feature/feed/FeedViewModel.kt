package com.shin.vicmusic.feature.feed

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.domain.ArtistUpdate
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.SystemRecommendation
import com.shin.vicmusic.core.domain.UserActivity
import com.shin.vicmusic.core.domain.UserPost
import com.shin.vicmusic.core.domain.usecase.UpdateBgImgUseCase
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.util.copyUriToCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authManager: AuthManager,
    private val updateBgImgUseCase: UpdateBgImgUseCase
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

    fun toggleLike(feed: Feed) {
        viewModelScope.launch {
            // Optimistic update
            updateFeedInState(feed.copy(
                isLiked = !feed.isLiked,
                likesCount = if (feed.isLiked) feed.likesCount - 1 else feed.likesCount + 1
            ))

            val result = feedRepository.toggleLikeFeed(feed.id)
            if (result is MyNetWorkResult.Error) {
                updateFeedInState(feed)
                _error.value = result.message
            }
        }
    }

    private fun updateFeedInState(updatedFeed: Feed) {
        _discoveryItems.value = _discoveryItems.value.map { if (it.id == updatedFeed.id) updatedFeed else it }
        _followingItems.value = _followingItems.value.map { if (it.id == updatedFeed.id) updatedFeed else it }
    }

    // Helper extension for copying Feed (since it's a sealed class)
    private fun Feed.copy(isLiked: Boolean, likesCount: Int): Feed {
        return when (this) {
            is UserPost -> this.copy(isLiked = isLiked, likesCount = likesCount)
            is UserActivity -> this.copy(isLiked = isLiked, likesCount = likesCount)
            is ArtistUpdate -> this.copy(isLiked = isLiked, likesCount = likesCount)
            is SystemRecommendation -> this.copy(isLiked = isLiked, likesCount = likesCount)
        }
    }


    fun updateUserBg(localPath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = updateBgImgUseCase(localPath,"user")) {
                is MyNetWorkResult.Success -> _message.value = "背景更新成功"
                is MyNetWorkResult.Error -> _error.value = result.message
            }

            _isLoading.value = false
        }
    }
    fun handleImageSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val localPath = copyUriToCache(context, uri)
            if (localPath != null) {
                updateUserBg(localPath)
            }
        }
    }
}