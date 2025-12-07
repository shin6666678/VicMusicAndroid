package com.shin.vicmusic.core.network.di

import com.shin.vicmusic.feature.player.PlayerViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// 定义 EntryPoint，用于在 Composable 中获取单例 PlayerViewModel
@EntryPoint
@InstallIn(SingletonComponent::class) // 与 PlayerViewModel 的 @Singleton 作用域一致
interface PlayerViewModelEntryPoint {
    fun getPlayerViewModel(): PlayerViewModel
}