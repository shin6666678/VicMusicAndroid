package com.shin.vicmusic.feature.playBackQueue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.Song

@Composable
fun IsPlayingQueue(
    queue:List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onRemoveSong: (Int) -> Unit,
){
// --- 正在播放列表 ---
    Column(modifier = Modifier.fillMaxSize()) {
        // 列表头信息（歌曲数量 + 播放模式）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "共 ${queue.size} 首",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            // 这里可以放置播放模式切换按钮，目前先留空或放个图标
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(queue.indices.toList()) { index ->
                val song = queue[index]
                val isPlaying = index == currentIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 播放指示器/序号
                    if (isPlaying) {
                        Text(
                            text = "Playing", // 或者使用动态图表
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(36.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.width(36.dp)
                        )
                    }

                    // 歌曲信息
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isPlaying) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "- 正在播放",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.artist.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 移除按钮
                    IconButton(
                        onClick = { onRemoveSong(index) },
                        modifier = Modifier.size(24.dp) // 稍微调小一点
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "移除",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}