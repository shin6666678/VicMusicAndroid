package com.shin.vicmusic.feature.playBackQueue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.design.theme.LocalAppColors // 关键：导入自定义颜色系统
import com.shin.vicmusic.feature.common.icon.PayTypeIcon

@Composable
fun IsPlayingQueue(
    queue: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onRemoveSong: (Int) -> Unit,
) {
    val colors = LocalAppColors.current
    val activeHighlightColor = colors.accentPrimary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // 使用 itemsIndexed 获取索引和对象，代码更简洁
            itemsIndexed(queue) { index, song ->
                val isPlaying = index == currentIndex

                val titleColor = if (isPlaying) activeHighlightColor else colors.textColor
                val artistColor = if (isPlaying)
                    activeHighlightColor.copy(alpha = 0.7f)
                else
                    colors.textColor.copy(alpha = 0.5f)

                val displayText = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = titleColor,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )) {
                        append(song.title)
                    }
                    withStyle(style = SpanStyle(
                        color = artistColor,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )) {
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
                    Row(
                        modifier = Modifier.weight(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PayTypeIcon(song = song)
                    }

                    Row(
                        modifier = Modifier.weight(0.3f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isPlaying) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOff,
                                contentDescription = "正在播放",
                                tint = activeHighlightColor, // 同步主题色
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = { /* TODO: 处理喜欢逻辑 */ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "喜欢",
                                // 喜欢状态固定红色，非喜欢状态使用主题图标色
                                tint = if (song.isLiked) Color(0xFFFE3C30) else colors.iconColor.copy(alpha = 0.6f),
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
                                tint = colors.iconColor.copy(alpha = 0.4f), // 使用主题中的图标色
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}