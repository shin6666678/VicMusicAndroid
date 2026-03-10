package com.shin.vicmusic.core.manager

import android.util.Log
import com.google.gson.Gson
import com.shin.vicmusic.core.config.Config.WS_URL
import com.shin.vicmusic.core.domain.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.Collections
import java.util.LinkedHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null
    val isConnected: Boolean get() = webSocket != null

    // 【1. 心跳保活】：每15秒系统底层自动发一次 Ping 帧，维持长连接
    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _incomingMessages = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<ChatMessage> = _incomingMessages.asSharedFlow()

    // --- 断线重连相关的协程 Job ---
    private var reconnectJob: Job? = null

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            Log.d("WebSocket", "5秒后尝试断线重连...")
            delay(5000) // 延迟5秒重连，防止网络彻底断掉时引发死循环雪崩
            connect()
        }
    }

    // LRU Cache 最多缓存最近 100 条消息的 ID用来去重
    private val messageIdCache = Collections.newSetFromMap(
        object : LinkedHashMap<String, Boolean>(64, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Boolean>): Boolean {
                return size > 100
            }
        }
    )

    fun connect() {
        if (webSocket != null) return

        scope.launch {
            val token = tokenManager.tokenFlow.firstOrNull()
            if (token.isNullOrBlank()) {
                Log.e("WebSocket", "Cannot connect: Token is null or empty")
                return@launch
            }

            val request = Request.Builder()
                .url("$WS_URL?token=$token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "Connected")
                    // 连接成功后，取消掉可能正在倒计时的重连任务
                    reconnectJob?.cancel()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val msg = gson.fromJson(text, ChatMessage::class.java)

                        val msgId = msg.id
                        if (msgId != null && !messageIdCache.contains(msgId)) {
                            messageIdCache.add(msgId) // 存入缓存
                            _incomingMessages.tryEmit(msg) // 派发给 UI
                        } else {
                            Log.d("WebSocket", "拦截到重复推送的消息 ID: $msgId")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "Error: ${t.message}")
                    this@WebSocketManager.webSocket = null
                    // 断线重连网络错误或意外断开时触发重连
                    scheduleReconnect()
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "Closed: $reason")
                    this@WebSocketManager.webSocket = null
                    //服务端意外关闭连接时触发重连
                    scheduleReconnect()
                }
            })
        }
    }

    fun sendMessage(msg: ChatMessage) {
        val json = gson.toJson(msg)
        webSocket?.send(json)
    }

    fun disconnect() {
        // 用户主动退出登录时，不要触发重连
        reconnectJob?.cancel()
        webSocket?.close(1000, "Logout")
        webSocket = null
    }
}