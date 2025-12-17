package com.shin.vicmusic.core.design.composition

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.PlaybackQueueManager
import com.shin.vicmusic.core.manager.PlayerManager
import com.shin.vicmusic.core.manager.TokenManager

// 定义一个 CompositionLocal，默认值抛出异常（强制要求上层提供）
val LocalPlayerManager = staticCompositionLocalOf<PlayerManager> {
    error("No PlayerManager provided! Make sure to wrap your app with CompositionLocalProvider.")
}
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

val LocalPlaybackQueueManager = staticCompositionLocalOf<PlaybackQueueManager> {
    error("No PlaybackQueueManager provided")
}

val LocalAuthManager = staticCompositionLocalOf<AuthManager> {
    error("No AuthManager provided")
}
val LocalTokenManager = staticCompositionLocalOf<TokenManager> {
    error("No TokenManager provided")
}