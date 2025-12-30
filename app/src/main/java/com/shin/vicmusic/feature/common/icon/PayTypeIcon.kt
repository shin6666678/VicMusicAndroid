package com.shin.vicmusic.feature.common.icon

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song

@Composable
fun PayTypeIcon(
    song: Song
){
// [修改] VIP 标签：描边风格 (Outlined Style)
    if (song.payType == PayType.VIP) {
        Box(
            modifier = Modifier
                .padding(end = 4.dp)
                // 红色或金色描边，1px 细线，圆角 2dp
                .border(1.dp, Color(0xFFFE3C30), RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 0.dp) // 极小的内部 Padding
        ) {
            Text(
                text = "VIP",
                fontSize = 8.sp, // 超小字体
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFE3C30), // 文字颜色与边框一致
                lineHeight = 8.sp, // 防止行高撑开
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }else if(song.payType == PayType.PAY){
        Box(
            modifier = Modifier
                .padding(end = 4.dp)
                // 灰色描边，1px 细线，圆角 2dp
                .border(1.dp, Color(0xFF12B2DE), RoundedCornerShape(3.dp))
                .padding(horizontal = 3.dp, vertical = 0.dp) // 极小的内部 Padding
        ) {
            Text(
                text = "付费",
                fontSize = 8.sp, // 超小字体
                fontWeight = FontWeight.Bold,
                color = Color(0xFF12B2DE), // 颜色与边框一致
            )
        }
    }
}