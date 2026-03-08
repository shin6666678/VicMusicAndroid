package com.shin.vicmusic.feature.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ChatRepository
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authManager: AuthManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val targetUserId: String = checkNotNull(savedStateHandle["userId"])
    val targetUserName: String = savedStateHandle["userName"] ?: "聊天"
    val currentUserId = authManager.currentUser.value?.id ?: ""

    var inputText by mutableStateOf("")

    val messagesFlow: StateFlow<List<ChatMessage>> = chatRepository
        .observeMessages(targetUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        chatRepository.connect()
        // 只负责触发同步。网络数据存入 Room 后，会自动流向 messagesFlow 更新 UI
        viewModelScope.launch {
            chatRepository.syncHistory(targetUserId, 1)
        }
    }

    fun send() {
        if (inputText.isBlank()) return
        chatRepository.sendMessage(targetUserId, inputText)
        inputText = ""
    }
}