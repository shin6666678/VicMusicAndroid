package com.shin.vicmusic.feature.playBackQueue

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.io.Files.append
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.songAuth.PayTypeIcon

@Preview
@Composable
fun IsPlayIngQueuePreview() {
    IsPlayingQueue(
        queue = SONGS,
        currentIndex = 1,
        onSongClick = {},
        onRemoveSong = {},
    )
}

@Composable
fun IsPlayingQueue(
    queue: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onRemoveSong: (Int) -> Unit,
) {
    // 定义靛青色 (Indigo) 用于高亮正在播放的歌曲
    val indigoColor = Color(0xFF005599)

    // --- 正在播放列表 ---
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 列表头信息（歌曲数量 + 播放模式）
        Row(
            modifier = Modifier
                .fillMaxWidth().background(Color.Gray)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("预留区")
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(queue.indices.toList()) { index ->
                val song = queue[index]
                val isPlaying = index == currentIndex

                // 根据播放状态决定颜色
                val titleColor = if (isPlaying) indigoColor else MaterialTheme.colorScheme.onSurface
                val artistColor = if (isPlaying) indigoColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                // 富文本：歌名和歌手
                val displayText = buildAnnotatedString {
                    // 歌名部分
                    withStyle(style = SpanStyle(
                        color = titleColor,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    ) {
                        append(song.title)
                    }
                    // 歌手部分
                    withStyle(style = SpanStyle(
                        color = artistColor,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                    ) {
                        append(" - ${song.artist.name}")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 歌曲信息 (歌名 - 歌手 在一行)
                    Row(
                        modifier = Modifier.weight(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // 意思：有优先权占满空间(weight 1f)，但如果字很少，就只占字那么宽(fill false)
                            modifier = Modifier.weight(1f,fill = false) // 关键：让文本占据剩余所有空间，迫使超长部分省略
                        )
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                        PayTypeIcon(song=song)
                    }
                    // [区域2] 操作按钮区：占据 30% 宽度
                    Row(
                        modifier = Modifier.weight(0.3f), // ⭐ 关键：权重 0.3
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End // 按钮靠右对齐
                    ) {
                        if (isPlaying) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOff, // 暂时用 Favorite 代表播放中
                                contentDescription = "正在播放",
                                tint = indigoColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(28.dp) // 稍微调小以适应 30% 空间
                        ) {
                            Icon(
                                imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "喜欢",
                                tint = if (song.isLiked) Color(0xFFFE3C30) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { onRemoveSong(index) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "移除",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}