package com.shin.vicmusic.feature.vip

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
    vipTagText: String,
    vipBgColor: Color,
    vipTextColor: Color,
    onClick:()->Unit={}
){
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