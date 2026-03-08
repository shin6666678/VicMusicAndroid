package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.database.entity.ChatMessageEntity
import com.shin.vicmusic.core.domain.ChatMessage

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        fromUserId = fromUserId,
        toUserId = toUserId,
        content = content,
        createTime = createTime
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id ?: java.util.UUID.randomUUID().toString(),
        fromUserId = fromUserId,
        toUserId = toUserId,
        content = content,
        createTime = createTime ?: System.currentTimeMillis()
    )
}
