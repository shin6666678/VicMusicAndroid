package com.shin.vicmusic.feature.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ChatRepository
import com.shin.vicmusic.core.data.repository.NotifyRepository
import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.api.NotifyDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageListUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotifyDto> = emptyList(),
    val chatSessions: List<ChatSession> = emptyList(),
    val error: String? = null,
    val tabIndex: Int = 0 // 0: 通知, 1: 私信
)

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageListUiState())
    val uiState: StateFlow<MessageListUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadChatSessions()
    }

    fun switchTab(index: Int) {
        _uiState.update { it.copy(tabIndex = index) }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
        }
    }
    fun clearUnread(userId: String) {
        _uiState.update { state ->
            val updatedList = state.chatSessions.map { session ->
                if (session.userId == userId) {
                    session.copy(unreadCount = 0)
                } else {
                    session
                }
            }
            state.copy(chatSessions = updatedList)
        }
    }
    fun loadChatSessions() {
        viewModelScope.launch {
            when (val result = chatRepository.getChatSessions()) {
                is Result.Success -> {
                    _uiState.update { it.copy(chatSessions = result.data) }
                }
                is Result.Error -> {
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}