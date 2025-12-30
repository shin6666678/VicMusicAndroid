package com.shin.vicmusic.feature.common.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VipIcon(
    vipLevelInt:Int=0,
    onClick:()->Unit={}
){
    val (vipTagText, vipBgColor, vipTextColor) = when {
        vipLevelInt >= 6 -> Triple("VIP $vipLevelInt", Color(0xFF000000), Color(0xFFFFD700)) // 黑金配色
        vipLevelInt >= 1 -> Triple("VIP $vipLevelInt", Color(0xFFD4AF37), Color.White) // 普通 VIP 金色
        else -> Triple("普通用户", Color.LightGray, Color.White) // 非 VIP 灰色
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick=onClick)
            .background(vipBgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = vipTagText, // 动态文本
            style = MaterialTheme.typography.labelSmall,
            color = vipTextColor // 动态颜色
        )
    }
}