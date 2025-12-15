package com.shin.vicmusic.core.design.composition

import androidx.compose.runtime.staticCompositionLocalOf
import com.shin.vicmusic.feature.player.PlayerManager

// 定义一个 CompositionLocal，默认值抛出异常（强制要求上层提供）
val LocalPlayerManager = staticCompositionLocalOf<PlayerManager> {
    error("No PlayerManager provided! Make sure to wrap your app with CompositionLocalProvider.")
}