package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.WebSocketManager
import com.shin.vicmusic.core.model.ChatMessage
import com.shin.vicmusic.core.model.api.ChatSessionDto
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: MyNetworkApiService,
    private val datasource: MyRetrofitDatasource,
    private val webSocketManager: WebSocketManager,
    private val authManager: AuthManager
) {
    fun connect() {
        webSocketManager.connect()
    }

    fun sendMessage(toUserId: String, content: String) {
        val fromId = authManager.currentUser.value?.id ?: return // 使用 currentUser 获取 ID
        val msg = ChatMessage(
            fromUserId = fromId,
            toUserId = toUserId,
            content = content
        )
        webSocketManager.sendMessage(msg)
    }

    suspend fun getHistory(targetUserId: String, page: Int): List<ChatMessage> {
        val res = apiService.getChatHistory(targetUserId, page, 50)
        return res.data ?: emptyList()
    }

    fun observeMessages(targetUserId: String): Flow<ChatMessage> {
        return webSocketManager.incomingMessages.filter {
            it.fromUserId == targetUserId || it.toUserId == targetUserId
        }
    }

    suspend fun getChatSessions(): Result<List<ChatSessionDto>> {
        val response = datasource.getChatSessions()
        return if (response.code == 0 && response.data != null) {
            Result.Success(response.data)
        } else {
            Result.Error(response.message ?: "获取会话失败")
        }

    }
}