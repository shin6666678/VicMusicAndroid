package com.shin.vicmusic.feature.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ChatRepository
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages
    var inputText by mutableStateOf("")

    init {
        chatRepository.connect()
        viewModelScope.launch {
            val history = withContext(Dispatchers.IO) { chatRepository.getHistory(targetUserId, 1) }
            _messages.addAll(history)
        }
        viewModelScope.launch {
            chatRepository.observeMessages(targetUserId).collect { _messages.add(it) }
        }
    }

    fun send() {
        if (inputText.isBlank()) return
        chatRepository.sendMessage(targetUserId, inputText)
        val tempMsg = ChatMessage(fromUserId = currentUserId, toUserId = targetUserId, content = inputText, createTime = System.currentTimeMillis())
        _messages.add(tempMsg)
        inputText = ""
    }
}