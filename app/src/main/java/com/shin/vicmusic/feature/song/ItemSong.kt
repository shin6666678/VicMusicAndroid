package com.shin.vicmusic.feature.song

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.songAuth.PayTypeIcon

// 1. 对外使用的有状态组件 (Stateful)
@Composable
fun ItemSong(
    song: Song,
    modifier: Modifier = Modifier,
) {
    val playerManager = LocalPlayerManager.current
    val actionManager = LocalSongActionManager.current

    ItemSongContent(
        song = song,
        modifier = modifier,
        onPlayClick = { playerManager.playSong(song) },
        onLikeClick = { actionManager.toggleLike(song) },
        onAddToQueueClick = { playerManager.addSongToQueue(song) }
    )
}

// 2. 纯 UI 组件 (Stateless) - 供预览和ItemSong调用
@Composable
private fun ItemSongContent(
    song: Song,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit,
    onLikeClick: () -> Unit,
    onAddToQueueClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onPlayClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyAsyncImage(
            model = song.icon,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PayTypeIcon(song = song)
                Text(
                    text = "${song.artist.name} - ${song.album.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "喜欢(Like)",
                    tint = if (song.isLiked) Color(0xFFFE3C30) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onAddToQueueClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多(More)",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// 3. 预览时调用 Stateless 组件
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreView() {
    VicMusicTheme {
        ItemSongContent(
            song = SONG.copy(
                payType = PayType.PAY,
                title = "这是一首名字非常非常长的歌曲测试溢出效果",
                artist = com.shin.vicmusic.core.domain.Artist("1", "周杰伦"),
                album = com.shin.vicmusic.core.domain.Album("1", "最伟大的作品")
            ),
            onPlayClick = {},
            onLikeClick = {},
            onAddToQueueClick = {}
        )
    }
}