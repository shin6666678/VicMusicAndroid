package com.shin.vicmusic.feature.common.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ... VicTextButton 保持不变 ...
@Composable
fun VicTextButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = text,
        fontSize = if (isSelected) 20.sp else 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) selectedColor else unselectedColor,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }
    )
}

// ================================================================
// 纯净的带角标图标 (不带点击事件，纯视觉展示)
// ================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VicBadgedIcon(
    icon: ImageVector,
    contentDescription: String?,
    badgeCount: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current // 默认使用当前上下文颜色
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (badgeCount > 0) {
                Badge {
                    val displayCount = if (badgeCount > 99) "99+" else badgeCount.toString()
                    Text(text = displayCount)
                }
            }
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

// ================================================================
// [修改后] 带点击事件的图标按钮
// 内部复用 VicBadgedIcon
// ================================================================
@Composable
fun VicIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    badgeCount: Int = 0,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    iconSize: Dp = 28.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp)
    ) {
        // 复用提取出来的逻辑
        VicBadgedIcon(
            icon = icon,
            contentDescription = contentDescription,
            badgeCount = badgeCount,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}