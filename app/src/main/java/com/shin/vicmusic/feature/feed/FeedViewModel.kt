package com.shin.vicmusic.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.FeedRepository
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    authManager: AuthManager
) : ViewModel() {

    // --- UI State ---
    private val _discoveryItems = MutableStateFlow<List<Feed>>(emptyList())
    val discoveryItems = _discoveryItems.asStateFlow()

    private val _followingItems = MutableStateFlow<List<Feed>>(emptyList())
    val followingItems = _followingItems.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    val currentUser = authManager.currentUser

    private val _headerBackgroundImage = MutableStateFlow("")
    val headerBackgroundImage = _headerBackgroundImage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

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
            _isLoading.value = true
            _error.value = null

            val result = if (_selectedTabIndex.value == 0) {
                feedRepository.getFeeds(1, 20) // TODO: Implement pagination
            } else {
                feedRepository.getFollowingFeeds(1, 20) // TODO: Implement pagination
            }

            when (result) {
                is Result.Success -> {
                    if (_selectedTabIndex.value == 0) {
                        _discoveryItems.value = result.data.list?:emptyList()
                    } else {
                        _followingItems.value = result.data.list?:emptyList()
                    }
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
}
