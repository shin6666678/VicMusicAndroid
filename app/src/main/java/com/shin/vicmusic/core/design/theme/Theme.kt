package com.shin.vicmusic.core.design.theme

import android.os.Build
import com.shin.vicmusic.core.design.theme.isAppInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Red,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = AppBackground,
    surface = AppBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimaryContainer=TextPrimary,
    surfaceVariant=AppBackground,
)

private val LightColorScheme = lightColorScheme(
    primary = Pink40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = AppBackground,
    surface = AppBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimaryContainer=TextPrimary,
    surfaceVariant=AppBackground,
)

@Composable
fun VicMusicTheme(
    darkTheme: Boolean = isAppInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val dividerColor=if(darkTheme)md_theme_dark_divider else md_theme_light_divider
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
