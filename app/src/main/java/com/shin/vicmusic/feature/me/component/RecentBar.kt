package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.common.item.ItemPlaylistSquare

@Composable
fun RecentBar(
    playlists: List<Playlist> = emptyList(), // 歌单列表
    recentNum: Int = 0,                      // 最近播放歌曲数量
    recentIcon: String = "",                 // 最近播放歌曲封面
    onRecentOrMoreClick: () -> Unit = {},    // 点击"全部已播"或"更多"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(12.dp)) // 稍微调整顶部间距

        // --- 顶部标题栏 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // 增加左右边距
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "最近播放",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onRecentOrMoreClick)
            ) {
                Text(
                    text = "更多",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "More",
                    tint = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // --- 横向滚动列表 ---
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Item 之间的间距
            contentPadding = PaddingValues(horizontal = 16.dp)   // 列表两端的 Padding
        ) {
            // 1. 第一个Item: 固定显示"全部已播" (Recent Songs)
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(100.dp) // 固定宽度，保持整齐
                        .clickable { onRecentOrMoreClick() }
                ) {
                    AsyncImage(
                        model = recentIcon.ifEmpty { "https://picsum.photos/100/100" },
                        contentDescription = "Recent Songs Cover",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)), // 圆角
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "已播歌曲${recentNum}首",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 2. 后续Items: 最近播放的歌单列表 (Playlists)
            items(playlists) { playlist ->
                ItemPlaylistSquare(playlist = playlist)
            }
        }
    }
}