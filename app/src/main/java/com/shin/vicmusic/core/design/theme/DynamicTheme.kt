package com.shin.vicmusic.core.design.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val glassWhite: Color,
    val glassBorder: Color,
    val inputBackground: Color,
    val textColor: Color,
    val iconColor: Color,
    val bottomBarBackground: Color,
    val bottomBarContent: Color,
    val songDetailBackground: Color,
    val dividerColor: Color,
    val songDetailIconColor : Color = Color(0xFFCBD5E1),
    val songDetailTextStart : Color = Color(0xFFF1F5F9),
    val songDetailTextEnd : Color = Color(0xFFCBD5E1),
)

val lightAppColors = AppColors(
    gradientStart = Color(0xFFF1F5F9),
    gradientMid = Color(0xFFE2E8F0),
    gradientEnd = Color(0xFFCBD5E1),
    accentPrimary = Color(0xFF60A5FA),
    accentSecondary = Color(0xFF34D399),
    glassWhite = Color(0x66FFFFFF),
    glassBorder = Color(0x26000000),
    inputBackground = Color(0x0D000000),
    textColor = Color(0xFF1E293B),
    iconColor = Color(0xFF1E293B),
    bottomBarBackground = Color(0xCCFFFFFF),
    bottomBarContent = Color(0xFF1E293B),
    songDetailBackground = Color(0xFFF8FAFC),
    dividerColor = Color(0xFFF5F5F5),
    songDetailIconColor = Color(0xFFCBD5E1),
)

val darkAppColors = AppColors(
    gradientStart = Color(0xFF020617),
    gradientMid = Color(0xFF0F172A),
    gradientEnd = Color(0xFF1E293B),
    accentPrimary = Color(0xFF3B82F6),
    accentSecondary = Color(0xFF2DD4BF),
    glassWhite = Color(0x1AFFFFFF),
    glassBorder = Color(0x33FFFFFF),
    inputBackground = Color(0x0DFFFFFF),
    textColor = Color.White,
    iconColor = Color.White,
    bottomBarBackground = Color(0xCC111827),
    bottomBarContent = Color.White,
    songDetailBackground = Color(0xFF1C1C1E),
    dividerColor = Color(0xFF000000),
    songDetailIconColor = Color(0xFFCBD5E1),
)

val redAppColors = AppColors(
    gradientStart = Color.White,
    gradientMid = Color.White,
    gradientEnd = Color.White,
    accentPrimary = Color.Red,
    accentSecondary = Color(0xFFFF6B6B), // A lighter red for secondary
    glassWhite = Color(0x1AFFFFFF),
    glassBorder = Color(0x33FF0000), // Reddish border for glass
    inputBackground = Color(0x0DFF0000),
    textColor = Color.Red,
    iconColor = Color.Red,
    bottomBarBackground = Color(0xCCFFFFFF),
    bottomBarContent = Color.Red,
    songDetailBackground = Color.White,
    dividerColor = Color(0xFFF5F5F5),
    songDetailIconColor = Color(0xFFCBD5E1),
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided!")
}
