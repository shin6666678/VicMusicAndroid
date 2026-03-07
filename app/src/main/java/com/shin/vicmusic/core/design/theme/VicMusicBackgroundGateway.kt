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
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.manager.BackgroundStyle
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
    // Collect the current background style as state, defaulting to DYNAMIC_GLOW
    val userStyle by mainViewModel.backgroundStyle.collectAsState()
    val isDark = LocalAppColors.current.isDark
    
    // Force DYNAMIC_GLOW if in Dark Mode, ignoring user style
    val currentStyle = if (isDark) BackgroundStyle.DYNAMIC_GLOW else userStyle

    Box(modifier = modifier.fillMaxSize()) {
        when (currentStyle) {
            BackgroundStyle.DYNAMIC_GLOW -> {
                AppBackground(content = content)
            }
            BackgroundStyle.SOLID_COLOR -> {
                Box(Modifier.fillMaxSize().background(LocalAppColors.current.gradientMid)) {
                    content()
                }
            }
            BackgroundStyle.NONE -> {
                // No specific background, relies on the underlying MaterialTheme surface
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    content()
                }
            }
        }
    }
}
