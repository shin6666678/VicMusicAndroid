package com.shin.vicmusic.feature.common.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.design.theme.LocalAppColors

@Composable
fun CommonTopBarSelect(
    backImageVictor: ImageVector =Icons.AutoMirrored.Filled.ArrowBack,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit={},
    tabs: List<BarTabItem> = emptyList(),
    actions: List<BarActionItem> = emptyList(),
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = LocalAppColors.current.textColor,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(backgroundColor)
            .padding(horizontal = 4.dp, vertical = 12.dp), // 调整了一下 padding，给左侧返回按钮留空间
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 左侧区域 (Fixed Back Icon) ---
        if(showBackButton){
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = contentColor
                )
            ) {
                Icon(
                    imageVector = backImageVictor,
                    contentDescription = "Back",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // --- 中间区域 (Center Tabs) ---
        // 使用 weight(1f) 占据中间所有空间，并设置居中对齐
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterHorizontally // [修改] 让内容在水平方向居中
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tabs) { tab ->
                Text(
                    text = tab.name,
                    fontSize = if (tab.isSelected) 20.sp else 16.sp,
                    fontWeight = if (tab.isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (tab.isSelected) contentColor else contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.clickable(
                        interactionSource = null,
                        indication = null
                    ) { tab.onClick() }
                )
            }
        }

        // --- 右侧区域 (Actions) ---
        // 为了保持视觉平衡，如果右侧没有图标，中间的文字可能会看起来稍微偏右（因为它在 返回键 和 屏幕边缘 之间居中）。
        // 如果需要严格的屏幕绝对居中，通常需要用 Box 布局，但目前的 Row 结构在有右侧图标时效果最好。
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (actions.isNotEmpty()) {
                actions.forEach { action ->
                    IconButton(
                        onClick = action.onClick,
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = contentColor
                        )
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}