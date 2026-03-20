package com.shin.vicmusic.core.design.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.theme.dressUp.dynamicGlow.DynamicGlowAppBackGround
import com.shin.vicmusic.core.manager.DressUpStyle
import com.shin.vicmusic.feature.main.MainViewModel

/**
 * A global gateway that applies the chosen background style.
 * It observes the user's preference from ThemeManager and renders the corresponding background.
 */
@Composable
fun VicMusicBackgroundGateway(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    content: @Composable BoxScope.() -> Unit
) {
    // Collect the current dress up style as state
    val currentStyle by mainViewModel.dressUpStyle.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (currentStyle) {
            DressUpStyle.SYSTEM_DEFAULT -> {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    content()
                }
            }
            DressUpStyle.LIGHT_GLOW, DressUpStyle.DARK_GLOW -> {
                DynamicGlowAppBackGround(content = content)
            }
            DressUpStyle.LIGHT_SOLID, DressUpStyle.DARK_SOLID -> {
                Box(Modifier.fillMaxSize().background(LocalAppColors.current.gradientMid)) {
                    content()
                }
            }
            DressUpStyle.LIGHT_NONE -> {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    content()
                }
            }
            DressUpStyle.DARK_NONE -> {
                Box(Modifier.fillMaxSize().background(LocalAppColors.current.gradientMid)) {
                    content()
                }
            }
            DressUpStyle.RED -> {
                Box(Modifier.fillMaxSize().background(Color.White)) {
                    content()
                }
            }
        }
    }
}
