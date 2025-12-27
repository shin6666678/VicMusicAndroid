package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.playlist.meList.PlaylistViewModel
import com.shin.vicmusic.feature.songAuth.PayTypeIcon

// 1. 对外使用的有状态组件 (Stateful)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSong(
    song: Song,
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val playerManager = LocalPlayerManager.current
    val actionManager = LocalSongActionManager.current
    var showPlaylistSheet by remember { mutableStateOf(false) }

    if (showPlaylistSheet) {
        val playlists by viewModel.playlists.collectAsState()
        LaunchedEffect(Unit) { viewModel.fetchMyPlaylists() }

        ModalBottomSheet(onDismissRequest = { showPlaylistSheet = false }) {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item {
                    Text("收藏到歌单(Add to Playlist)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
                }
                items(playlists) { playlist ->
                    ItemPlaylist(playlist = playlist, onClick = {
                        viewModel.addSongToPlaylist(playlist.id, song.id)
                        showPlaylistSheet = false
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    ItemSongContent(
        song = song,
        modifier = modifier,
        onPlayClick = { playerManager.playSong(song) },
        onLikeClick = { actionManager.toggleLike(song) },
        onAddToQueueClick = { playerManager.addSongToQueue(song) },
        onAddToPlaylistClick = { showPlaylistSheet = true }
    )
}

// 2. 纯 UI 组件 (Stateless) - 供预览和ItemSong调用
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSongContent(
    song: Song,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit={},
    onLikeClick: () -> Unit={},
    onAddToQueueClick: () -> Unit={},
    onAddToPlaylistClick: () -> Unit={}
) {
    var showSheet by remember { mutableStateOf(false) } // 控制弹窗显示(Show Sheet)

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
            // 点击触发底部弹窗
            IconButton(onClick = { showSheet = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.MoreVert, "更多(More)", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                // 1. 顶部歌曲信息(Header Info)
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    MyAsyncImage(model = song.icon, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(" ${song.title}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(song.artist.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                // 2. 功能图标网格(Action Grid)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomSheetActionItem(Icons.Default.PlayArrow, "下一首播放",
                        modifier = Modifier.clickable {
                            showSheet = false
                            onAddToQueueClick()
                        })
                    BottomSheetActionItem(Icons.Default.FavoriteBorder, "收藏")
                    BottomSheetActionItem(Icons.Default.Add, "加入歌单",
                        modifier = Modifier.clickable {
                            showSheet = false // 关闭当前菜单
                            onAddToPlaylistClick() // 触发歌单选择弹窗
                        })
                    BottomSheetActionItem(Icons.Default.Share, "分享")
                    BottomSheetActionItem(Icons.Default.ArrowDropDown, "下载")
                }

                // 3. 列表选项(List Options)
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("设置铃声", modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth())
                    Text("单曲购买", modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth())
                }
            }
        }
    }
}
// 辅助组件(Helper Composable)
@Composable
fun BottomSheetActionItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.Black)
        }
        Spacer(Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
                artist = Artist("1", "周杰伦"),
                album = Album("1", "最伟大的作品")
            ),
            onPlayClick = {},
            onLikeClick = {},
            onAddToQueueClick = {},
            onAddToPlaylistClick = {}
        )
    }
}