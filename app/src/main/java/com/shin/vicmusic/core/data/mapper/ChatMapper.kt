package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.model.api.ChatSessionDto

fun ChatSessionDto.toDomain(): ChatSession{
    return ChatSession(
        userId = userId,
        username = username,
        avatar = avatar,
        lastMessage = lastMessage,
        lastTime = lastTime,
        unreadCount = unreadCount
    )
}