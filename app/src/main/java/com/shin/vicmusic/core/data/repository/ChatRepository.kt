package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.ChatMessage
import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.WebSocketManager
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

    suspend fun getChatSessions(): MyNetWorkResult<List<ChatSession>> {
        val dtoResponse = datasource.getChatSessions()
        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data
            val domainList = dtoList.map { it.toDomain()}
            return MyNetWorkResult.Success(domainList)
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "未知错误")

    }
}