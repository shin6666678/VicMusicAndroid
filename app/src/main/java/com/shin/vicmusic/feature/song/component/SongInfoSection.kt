package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.shin.vicmusic.core.domain.Song

@Composable
fun SongInfoSection(song: Song, modifier: Modifier = Modifier, onLikeClick: () -> Unit = {}) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = song.title, // 歌曲标题
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), // 加粗标题
                //color = MaterialTheme.colorScheme.onSurface,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onLikeClick, modifier = Modifier.size(24.dp)) { // 点赞/收藏按钮
                Icon(Icons.Default.FavoriteBorder, contentDescription = "喜欢")
                Text(text = "10w+", fontSize = 10.sp) // 示例数据
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp, // 横向间距
            crossAxisSpacing = 4.dp // 纵向间距
        ) { // 标签区域
            TagChip(text = song.artist.name) // 艺术家作为标签
            TagChip(text = "387人在听") // 示例数据
            TagChip(text = "标准") // 示例数据
            TagChip(text = "VIP") // 示例数据
            TagChip(text = "原声") // 示例数据
            TagChip(text = "视频") // 示例数据
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "曲: ${song.artist}", // 曲信息
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            // 假设这里可以有其他信息
        }
    }
}

@Composable
fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}