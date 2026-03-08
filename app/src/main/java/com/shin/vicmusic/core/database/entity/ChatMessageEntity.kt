package com.shin.vicmusic.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val content: String,
    val createTime: Long
)
