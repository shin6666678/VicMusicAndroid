package com.shin.vicmusic.feature.song

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.design.component.MyAsyncImage
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.songAuth.PayTypeIcon

@Composable
fun ItemSong(
    song: Song,
    modifier: Modifier = Modifier,
    onAddToQueueClick: (Song) -> Unit = {},
    onLikeClick: (Song) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // [调整] 增加整体内边距，更舒适
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 封面图：增加圆角，提升质感
        MyAsyncImage(
            model = song.icon,
            modifier = Modifier
                .size(48.dp) // [调整] 稍微缩小一点点，符合标准列表尺寸
                .clip(RoundedCornerShape(8.dp)) // [新增] 8dp 圆角
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 2. 中间信息区域
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 歌名
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge, // 使用 bodyLarge
                fontWeight = FontWeight.Medium, // [调整] 稍微加粗一点
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 歌手信息行 + VIP 标签
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PayTypeIcon(song=song)

                // 歌手 - 专辑
                Text(
                    text = "${song.artist.name} - ${song.album.title}",
                    style = MaterialTheme.typography.bodySmall,
                    // [重要] 使用 onSurfaceVariant (次级文本颜色，通常是灰色)，显出层次感
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 3. 右侧操作区 (点赞 + 更多)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 喜欢按钮
            IconButton(
                onClick = { onLikeClick(song) },
                modifier = Modifier.size(32.dp) // 调整按钮触摸区域大小
            ) {
                Icon(
                    imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "喜欢",
                    tint = if (song.isLiked) Color(0xFFFE3C30) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp) // 图标本身小一点
                )
            }

            // 更多/添加 按钮
            IconButton(
                onClick = { onAddToQueueClick(song) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert, // 或者用 Add，看你需求，MoreVert 更像原生列表
                    contentDescription = "更多",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                        .clickable { onAddToQueueClick(song) } // 整个条目可点击播放,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreView() {
    VicMusicTheme {
        ItemSong(
            // 模拟一个 VIP 歌曲，名字长一点测试溢出
            song = SONG.copy(
                payType = PayType.PAY,
                title = "这是一首名字非常非常长的歌曲测试溢出效果",
                artist = com.shin.vicmusic.core.domain.Artist("1", "周杰伦"),
                album = com.shin.vicmusic.core.domain.Album("1", "最伟大的作品")
            )
        )
    }
}