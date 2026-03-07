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
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    SettingScreen(
        onBackClick = navController::popBackStack,
        onLogoutClick = viewModel::logout,
        onDebugCheckMessage = viewModel::triggerMessageCheck
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDebugCheckMessage: () -> Unit = {}
) {
    var showThemeDialog by remember { mutableStateOf(false) }

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