package com.shin.vicmusic.feature.me.recentPlay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.HistoryRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentPlayViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val authManager: AuthManager
) : ViewModel() {

    var songList by mutableStateOf<List<Song>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        // 监听退出登录事件，及时清空最近播放列表
        viewModelScope.launch {
            authManager.isLoggedIn.collect { loggedIn ->
                if (loggedIn == false) {
                    songList = emptyList()
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            val res = historyRepository.getHistory()
            if (res is MyNetWorkResult.Success) {
                songList = res.data
            }
            isLoading = false
        }
    }
}