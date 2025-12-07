package com.shin.vicmusic.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.shin.vicmusic.core.network.di.PlayerViewModelEntryPoint
import com.shin.vicmusic.feature.player.PlayerViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getPlayerViewModelSingleton(): PlayerViewModel {
    val context = LocalContext.current
    // 通过 EntryPointAccessors 获取 EntryPoint
    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        PlayerViewModelEntryPoint::class.java
    )
    return entryPoint.getPlayerViewModel()
}