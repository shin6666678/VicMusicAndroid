package com.shin.vicmusic.feature.song

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.model.Song

@Composable
fun PlaybackQueueSheet(
    // 假设从 PlayerViewModel 获取的完整队列
    queue: List<Song>,
    // 当前播放歌曲的索引
    currentIndex: Int,
    // 切换播放模式的回调 (顺序/随机/单曲循环)
    onToggleRepeatMode: () -> Unit={},
    // 点击列表歌曲的回调，需要告诉 PlayerViewModel 播放这个索引的歌曲
    onSongClick: (Int) -> Unit,
    // ⭐ [新增] 移除歌曲的回調參數
    onRemoveSong: (Int) -> Unit,
    // 关闭抽屉的回调
    onClose: () -> Unit,
    modifier: Modifier,
) {
    // ⭐ 使用 ModalBottomSheetLayout 或 ModalBottomSheet 来实现底部抽屉 UI
    // 这里我们使用一个简化的 Box 占位符和 LazyColumn

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f) // 占屏幕高度的 60%
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // 头部：标题和模式切换
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "当前播放 (${queue.size})", style = MaterialTheme.typography.titleLarge)
            // 模式切换按钮 (Placeholder)
            IconButton(onClick = onToggleRepeatMode) {
                // 假设有一个图标来表示当前模式
                Icon(Icons.Default.List, contentDescription = "切换播放模式")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 队列列表
        LazyColumn {
            items(queue.indices.toList()) { index ->
                val song = queue[index]
                val isPlaying = index == currentIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) } // 点击后播放该索引的歌曲
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 显示播放图标或索引号
                    Text(
                        text = if (isPlaying) "▶" else "${index + 1}.",
                        color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(30.dp)
                    )

                    // 歌曲信息
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // ⭐ [修改] 實作移除按鈕
                    IconButton(onClick = { onRemoveSong(index) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "移除",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}