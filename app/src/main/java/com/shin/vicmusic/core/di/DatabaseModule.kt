package com.shin.vicmusic.core.di

import android.content.Context
import androidx.room.Room
import com.shin.vicmusic.core.database.AppDatabase
import com.shin.vicmusic.core.database.dao.ChatMessageDao
import com.shin.vicmusic.core.database.dao.LocalMusicDao
import com.shin.vicmusic.core.database.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vicmusic_database"
        ).build()
    }

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    fun provideChatMessageDao(appDatabase: AppDatabase): ChatMessageDao {
        return appDatabase.chatMessageDao()
    }

    @Provides
    fun provideLocalMusicDao(appDatabase: AppDatabase): LocalMusicDao {
        return appDatabase.localMusicDao()
    }
}
