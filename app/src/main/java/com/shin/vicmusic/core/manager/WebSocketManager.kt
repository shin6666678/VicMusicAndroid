package com.shin.vicmusic.core.manager

import android.util.Log
import com.google.gson.Gson
import com.shin.vicmusic.core.domain.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null
    val isConnected: Boolean get() = webSocket != null
    private val client = OkHttpClient()
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _incomingMessages = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<ChatMessage> = _incomingMessages.asSharedFlow()

    fun connect() {
        if (webSocket != null) return

        scope.launch {
            val token = tokenManager.tokenFlow.firstOrNull()
            if (token.isNullOrBlank()) {
                Log.e("WebSocket", "Cannot connect: Token is null or empty")
                return@launch
            }

            val request = Request.Builder()
                .url("ws://115.190.155.131:9001/ws/chat?token=$token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "Connected")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val msg = gson.fromJson(text, ChatMessage::class.java)
                        _incomingMessages.tryEmit(msg)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "Error: ${t.message}")
                    this@WebSocketManager.webSocket = null
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    this@WebSocketManager.webSocket = null
                }
            })
        }
    }

    fun sendMessage(msg: ChatMessage) {
        val json = gson.toJson(msg)
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "Logout")
        webSocket = null
    }
}