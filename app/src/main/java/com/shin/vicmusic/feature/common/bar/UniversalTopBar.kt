package com.shin.vicmusic.feature.common.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.feature.common.common.VicIconButton
import com.shin.vicmusic.feature.common.common.VicTextButton

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
            // 示例：显示 5 条未读消息
            BarActionItem(Icons.Default.MailOutline, "Mail", 5) { },
            // 示例：超过 99 条显示 99+
            BarActionItem(Icons.Default.Tune, "Settings", 100) { }
        ),
    )
}

// 左侧的 Tab 数据
data class BarTabItem(
    val name: String,
    val isSelected: Boolean = false,
    val onClick: () -> Unit
)

// 右侧的 Action 数据 (contentNumber > 0 时显示角标)
data class BarActionItem(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val contentNumber: Int = 0,
    val onClick: () -> Unit
)

@Composable
fun UniversalTopBar(
    tabs: List<BarTabItem>,
    actions: List<BarActionItem> = emptyList(),
    backgroundColor: Color = Color.Transparent,
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
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tabs) { tab ->
                VicTextButton(
                    text = tab.name,
                    isSelected = tab.isSelected,
                    onClick = tab.onClick,
                    selectedColor = contentColor,
                    unselectedColor = contentColor.copy(alpha = 0.6f)
                )
            }
        }

        if (actions.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                actions.forEach { action ->
                    VicIconButton(
                        icon = action.icon,
                        onClick = action.onClick,
                        contentDescription = action.contentDescription,
                        badgeCount = action.contentNumber,
                        tint = contentColor
                    )
                }
            }
        }
    }
}