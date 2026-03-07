package com.shin.vicmusic.core.design.theme

import android.os.Build
import com.shin.vicmusic.core.design.theme.isAppInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

@Composable
fun VicMusicTheme(
    darkTheme: Boolean = isAppInDarkTheme(),
    appColors: AppColors = if (darkTheme) darkAppColors else lightAppColors,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
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
    } else {
        lightColorScheme(
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
    }

    val dividerColor = if (darkTheme) md_theme_dark_divider else md_theme_light_divider
    CompositionLocalProvider(
        LocalDividerColor provides dividerColor
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
