package com.shin.vicmusic.feature.rankList.rankListDetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.feature.common.CommonTopBar

@Preview
@Composable
fun RankListDetailHeaderPreview() {
    RankListDetailHeader()
}

@Composable
fun RankListDetailHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // 根据需要调整高度
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB0C4DE), Color(0xFFE6E6FA)) // 示例渐变颜色
                )
            )
    ) {
        CommonTopBar(
            midText = "维克音乐排行榜",
            containerColor = Color.Transparent
        )

        // 主要内容 (文本和专辑封面)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "热歌榜",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "最近更新12月20日",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // 带有唱片效果的专辑封面
            Box(
                modifier = Modifier
                    .size(120.dp)
            ) {
                // 唱片背景
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6A5ACD)), // 唱片深紫色
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.Center)
                        .shadow(8.dp, RoundedCornerShape(8.dp), clip = false)
                ) {
                    // 这个卡片仅用于唱片的阴影和背景颜色
                }

                // 专辑封面
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .padding(bottom = 8.dp, start = 8.dp) // 调整以显示“唱片”在后面
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                ) {
                    Image(
                        imageVector = Icons.Filled.List, // 替换为实际专辑封面图片
                        contentDescription = "专辑封面",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}