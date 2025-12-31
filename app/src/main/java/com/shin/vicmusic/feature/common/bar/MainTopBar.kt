package com.shin.vicmusic.feature.common.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun UniversalTopBarPreview() {
    UniversalTopBar(
        tabs = listOf(
            BarTabItem("推荐", true) { },
            BarTabItem("歌单", false) { },
            BarTabItem("歌手", false) { },
            BarTabItem("排行", false) { },
            BarTabItem("最新", false) { }
        ),
        actions = listOf(
            BarActionItem(Icons.Default.MailOutline, "Mail") { },
            BarActionItem(Icons.Default.Tune, "Settings") { }
        ),
    )
}

// 左侧的 Tab 数据 (文字 + 点击逻辑 + 选中状态)
data class BarTabItem(
    val name: String,
    val isSelected: Boolean = false,
    val onClick: () -> Unit
)

// 右侧的 Action 数据 (图标 + 点击逻辑)
data class BarActionItem(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val onClick: () -> Unit
)

@Composable
fun UniversalTopBar(
    tabs: List<BarTabItem>,       // 左侧 Tab 列表
    actions: List<BarActionItem> = emptyList(), // 右侧图标列表
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 左侧区域 (LazyRow) ---
        // 使用 weight(1f) 让它占据除右侧图标外的所有剩余空间
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp), // Tab 之间的间距
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tabs) { tab ->
                Text(
                    text = tab.name,
                    fontSize = if (tab.isSelected) 20.sp else 16.sp, // 选中变大
                    fontWeight = if (tab.isSelected) FontWeight.Bold else FontWeight.Normal, // 选中加粗
                    color = if (tab.isSelected) contentColor else contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.clickable(
                        interactionSource = null,
                        indication = null // 移除点击水波纹
                    ) { tab.onClick() }
                )
            }
        }

        // --- 右侧区域 (Icons) ---
        // 如果 actions 不为空，自然会在右侧显示
        if (actions.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                actions.forEach { action ->
                    IconButton(
                        onClick = action.onClick,
                        modifier = Modifier.size(40.dp) // 控制点击区域大小
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                            tint = contentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}