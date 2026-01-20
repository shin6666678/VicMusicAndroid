package com.shin.vicmusic.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val systemRepository: SystemRepository
) : ViewModel() {

    // Add a state to indicate if the view model is ready.
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    // 全局未读数 (私信 + 通知)
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        refreshUnreadCount()
    }

    fun refreshUnreadCount() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val count = systemRepository.getGlobalUnreadCount()
                _unreadCount.value = count
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isReady.value = true
            }
        }
    }
}