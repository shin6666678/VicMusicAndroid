package com.shin.vicmusic.feature.rankList.rankList.component

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.domain.RankTopItem
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun RankListItem(
    modifier: Modifier = Modifier,
    rankListItemInfo: RankListPeak
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 封面图区域
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            MyAsyncImage(
                model = rankListItemInfo.imageUrl,
                modifier = Modifier.fillMaxSize()
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 榜单前三名列表
        Column {
            Text(
                text = rankListItemInfo.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // 显示更新频率 (如果 JSON 数据里有这个字段)
            if (rankListItemInfo.updateFrequency.isNotEmpty()) {
                Text(
                    text = rankListItemInfo.updateFrequency,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // [修改] 直接遍历 RankTopItem，不需要判断 "is Song"
            rankListItemInfo.items.forEachIndexed { index, item ->
                // item 是 RankTopItem 类型，直接取 title 和 artist (String)
                Text(
                    text = "${index + 1}. ${item.title} - ${item.artist}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1 // 防止过长换行
                )
            }
        }
    }
}

@Preview
@Composable
private fun RankListItemPreview() {
    // [修改] Preview 数据也要适配新的模型
    RankListItem(
        rankListItemInfo = RankListPeak(
            id = "1",
            imageUrl = "",
            title = "热歌榜",
            updateFrequency = "每日更新",
            items = listOf(
                RankTopItem("1", "关键词", "林俊杰"),
                RankTopItem("2", "稻香", "周杰伦"),
                RankTopItem("3", "晴天", "周杰伦")
            )
        )
    )
}