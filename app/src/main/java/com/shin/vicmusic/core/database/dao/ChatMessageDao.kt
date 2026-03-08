package com.shin.vicmusic.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shin.vicmusic.core.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("""
        SELECT * FROM chat_message 
        WHERE (fromUserId = :currentUserId AND toUserId = :targetUserId) 
           OR (fromUserId = :targetUserId AND toUserId = :currentUserId) 
        ORDER BY createTime DESC 
        LIMIT 100
    """)
    fun observeMessages(currentUserId: String, targetUserId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("""
        SELECT * FROM chat_message 
        WHERE (fromUserId = :currentUserId AND toUserId = :targetUserId) 
           OR (fromUserId = :targetUserId AND toUserId = :currentUserId) 
        ORDER BY createTime DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getMessages(currentUserId: String, targetUserId: String, limit: Int, offset: Int): List<ChatMessageEntity>
}