package com.shin.vicmusic.feature.common.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    backImageVictor: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    tabs: List<BarTabItem> = emptyList(),
    actions: List<BarActionItem> = emptyList(),
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = LocalAppColors.current.textColor,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(backgroundColor)
            .height(56.dp) // 标准 TopAppBar 高度
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. 左侧区域 (Alignment.CenterStart) ---
        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterStart),
                colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
            ) {
                Icon(
                    imageVector = backImageVictor,
                    contentDescription = "Back",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // --- 2. 中间区域 (绝对居中) ---
        LazyRow(
            modifier = Modifier
                .padding(horizontal = 80.dp), // 留出左右边距，防止 Tabs 盖住图标
            horizontalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterHorizontally
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

        // --- 3. 右侧区域 (Alignment.CenterEnd) ---
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            actions.forEach { action ->
                IconButton(
                    onClick = action.onClick,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}