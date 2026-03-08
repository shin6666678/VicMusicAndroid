package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.data.mapper.toEntity
import com.shin.vicmusic.core.database.dao.ChatMessageDao
import com.shin.vicmusic.core.domain.ChatMessage
import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.WebSocketManager
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val webSocketManager: WebSocketManager,
    private val authManager: AuthManager,
    private val chatMessageDao: ChatMessageDao
) {
    // 1. 创建 Repository 专属的受管协程作用域。
    // SupervisorJob 保证了如果某一条消息发送异常，不会导致整个 Scope 崩溃，其他监听任务能继续存活。
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // 使用受管作用域替代野协程
        repositoryScope.launch {
            webSocketManager.incomingMessages.collect { msg ->
                chatMessageDao.insertMessage(msg.toEntity())
            }
        }
    }

    fun connect() {
        webSocketManager.connect()
    }

    fun sendMessage(toUserId: String, content: String) {
        val fromId = authManager.currentUser.value?.id ?: return
        val msg = ChatMessage(
            fromUserId = fromId,
            toUserId = toUserId,
            content = content,
            createTime = System.currentTimeMillis()
        )

        repositoryScope.launch {
            chatMessageDao.insertMessage(msg.toEntity())
            try {
                webSocketManager.sendMessage(msg)
            } catch (e: Exception) {
                // 发生异常时可以更新数据库，将这条消息标记为“发送失败”
            }
        }
    }
    suspend fun syncHistory(targetUserId: String, page: Int): Result<Unit> {
        return try {
            val res = datasource.getChatHistory(targetUserId, page, 50)
            val remoteMessages = res.data ?: emptyList()
            if (remoteMessages.isNotEmpty()) {
                chatMessageDao.insertMessages(remoteMessages.map { it.toEntity() })
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Compose 唯一需要监听的数据源口子。
     */
    fun observeMessages(targetUserId: String): Flow<List<ChatMessage>> {
        val currentUserId = authManager.currentUser.value?.id ?: ""
        return chatMessageDao.observeMessages(currentUserId, targetUserId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getChatSessions(): MyNetWorkResult<List<ChatSession>> {
        return try {
            val dtoResponse = datasource.getChatSessions()
            if (dtoResponse.code == 0 && dtoResponse.data != null) {
                val domainList = dtoResponse.data.map { it.toDomain() }
                MyNetWorkResult.Success(domainList)
            } else {
                MyNetWorkResult.Error(dtoResponse.message ?: "未知错误")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error("网络连接失败，请检查网络: ${e.localizedMessage}")
        }
    }
}