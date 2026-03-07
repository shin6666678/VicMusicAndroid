package com.shin.vicmusic.feature.me.setting

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.design.theme.AppBackground
import com.shin.vicmusic.core.manager.AppThemeMode
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val currentThemeMode by viewModel.currentThemeMode.collectAsState(initial = AppThemeMode.SYSTEM)
    val currentBackgroundStyle by viewModel.currentBackgroundStyle.collectAsState(initial = com.shin.vicmusic.core.manager.BackgroundStyle.DYNAMIC_GLOW)
    SettingScreen(
        currentThemeMode = currentThemeMode,
        currentBackgroundStyle = currentBackgroundStyle,
        onThemeChanged = viewModel::updateTheme,
        onBackgroundStyleChanged = viewModel::updateBackgroundStyle,
        onBackClick = navController::popBackStack,
        onLogoutClick = viewModel::logout,
        onDebugCheckMessage = viewModel::triggerMessageCheck
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    currentThemeMode: AppThemeMode,
    currentBackgroundStyle: com.shin.vicmusic.core.manager.BackgroundStyle,
    onThemeChanged: (AppThemeMode) -> Unit,
    onBackgroundStyleChanged: (com.shin.vicmusic.core.manager.BackgroundStyle) -> Unit,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDebugCheckMessage: () -> Unit = {}
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showBackgroundDialog by remember { mutableStateOf(false) }

    val appColors = LocalAppColors.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CommonTopBar(midText = "设置", popBackStack = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SettingItem(title = "账号安全")
            SettingItem(title = "消息通知")
            SettingItem(title = "隐私设置")
            SettingItem(title = "通用")

            val themeText = when (currentThemeMode) {
                AppThemeMode.SYSTEM -> "跟随系统"
                AppThemeMode.LIGHT -> "浅色模式"
                AppThemeMode.DARK -> "深色模式"
            }
            SettingItem(title = "外观设置", subtitle = themeText, onClick = { showThemeDialog = true })

            // Only show "装扮" (Dress up) in light mode
            if (!appColors.isDark) {
                val backgroundText = when (currentBackgroundStyle) {
                    com.shin.vicmusic.core.manager.BackgroundStyle.DYNAMIC_GLOW -> "动态光晕"
                    com.shin.vicmusic.core.manager.BackgroundStyle.SOLID_COLOR -> "纯色背景"
                    com.shin.vicmusic.core.manager.BackgroundStyle.NONE -> "无背景"
                }
                SettingItem(title = "装扮", subtitle = backgroundText, onClick = { showBackgroundDialog = true })
            }

            val context = androidx.compose.ui.platform.LocalContext.current
            SettingItem(title = "【Debug】立即检查消息", onClick = onDebugCheckMessage)
            SettingItem(
                title = "允许后台运行 (忽略电池优化)",
                onClick = {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    context.startActivity(intent)
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 8.dp, color = appColors.textColor.copy(0.05f))
            SettingItem(title = "切换账号")
            SettingItem(title = "退出登录", textColor = Color.Red, onClick = onLogoutClick)
        }

        var showDarkThemeWarningDialog by remember { mutableStateOf(false) }

        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("选择外观主题") },
                text = {
                    Column {
                        ThemeOptionItem(text = "跟随系统", selected = currentThemeMode == AppThemeMode.SYSTEM) {
                            onThemeChanged(AppThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                        ThemeOptionItem(text = "浅色模式", selected = currentThemeMode == AppThemeMode.LIGHT) {
                            onThemeChanged(AppThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                        ThemeOptionItem(text = "深色模式", selected = currentThemeMode == AppThemeMode.DARK) {
                            if (currentThemeMode != AppThemeMode.DARK) {
                                showDarkThemeWarningDialog = true
                            }
                            showThemeDialog = false
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("关闭")
                    }
                }
            )
        }

        if (showDarkThemeWarningDialog) {
            AlertDialog(
                onDismissRequest = { showDarkThemeWarningDialog = false },
                title = { Text("提示") },
                text = { Text("切换到深色模式会取消当前应用的主题装扮，是否继续？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onThemeChanged(AppThemeMode.DARK)
                            showDarkThemeWarningDialog = false
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDarkThemeWarningDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showBackgroundDialog) {
            AlertDialog(
                onDismissRequest = { showBackgroundDialog = false },
                title = { Text("选择装扮样式") },
                text = {
                    Column {
                        ThemeOptionItem(text = "动态光晕", selected = currentBackgroundStyle == com.shin.vicmusic.core.manager.BackgroundStyle.DYNAMIC_GLOW) {
                            onBackgroundStyleChanged(com.shin.vicmusic.core.manager.BackgroundStyle.DYNAMIC_GLOW)
                            showBackgroundDialog = false
                        }
                        ThemeOptionItem(text = "纯色背景", selected = currentBackgroundStyle == com.shin.vicmusic.core.manager.BackgroundStyle.SOLID_COLOR) {
                            onBackgroundStyleChanged(com.shin.vicmusic.core.manager.BackgroundStyle.SOLID_COLOR)
                            showBackgroundDialog = false
                        }
                        ThemeOptionItem(text = "无背景", selected = currentBackgroundStyle == com.shin.vicmusic.core.manager.BackgroundStyle.NONE) {
                            onBackgroundStyleChanged(com.shin.vicmusic.core.manager.BackgroundStyle.NONE)
                            showBackgroundDialog = false
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showBackgroundDialog = false }) {
                        Text("关闭")
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeOptionItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun SettingItem(title: String, subtitle: String? = null, textColor: Color = Color.Unspecified, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = textColor)
        if (subtitle != null) {
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
        }
    }
}