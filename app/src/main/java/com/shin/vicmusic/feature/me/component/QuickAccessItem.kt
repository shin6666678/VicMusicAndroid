package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// 文件: app/src/main/java/com/shin/vicmusic/feature/me/MeRoute.kt

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    text: String,
    count: String,
    // [新增] 添加颜色参数，给予一个默认值（使用当前上下文的默认颜色，通常是黑色或深灰色）
    iconColor: Color = LocalContentColor.current,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(28.dp),
            // [修改] 将 tint 设置为传入的 iconColor
            tint = iconColor
        )
        Spacer(Modifier.height(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
        Text(text = count, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}