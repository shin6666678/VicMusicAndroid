package com.shin.vicmusic.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shin.vicmusic.core.database.dao.ChatMessageDao
import com.shin.vicmusic.core.database.dao.LocalMusicDao
import com.shin.vicmusic.core.database.dao.SearchHistoryDao
import com.shin.vicmusic.core.database.entity.ChatMessageEntity
import com.shin.vicmusic.core.database.entity.LocalMusicEntity
import com.shin.vicmusic.core.database.entity.SearchHistoryEntity

@Database(
    entities = [
        SearchHistoryEntity::class,
        ChatMessageEntity::class,
        LocalMusicEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun localMusicDao(): LocalMusicDao
}
