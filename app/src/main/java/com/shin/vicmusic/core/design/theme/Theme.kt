package com.shin.vicmusic.core.design.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun VicMusicTheme(
    appColors: AppColors = lightAppColors,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appColors.accentPrimary,
        onPrimary = Color.White,
        secondary = appColors.accentSecondary,
        background = Color.Transparent,
        surface = Color.Transparent,
        onBackground = appColors.textColor,
        onSurface = appColors.textColor,
        surfaceVariant = Color.Transparent,
        onSurfaceVariant = appColors.textColor.copy(alpha = 0.7f),
        outline = appColors.glassBorder
    )

    CompositionLocalProvider(
        LocalDividerColor provides appColors.dividerColor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            shapes = myShapes
        )
    }
}
val LocalDividerColor= staticCompositionLocalOf { md_theme_light_divider }
