package com.shin.vicmusic.core.design

import androidx.compose.runtime.staticCompositionLocalOf
import com.shin.vicmusic.feature.main.MainViewModel

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("No MainViewModel provided")
}