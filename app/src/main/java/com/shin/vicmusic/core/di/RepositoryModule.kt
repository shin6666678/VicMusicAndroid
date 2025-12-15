package com.shin.vicmusic.core.di

import com.shin.vicmusic.core.data.repository.PlayerRepository
import com.shin.vicmusic.core.data.repository.PlayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository
}