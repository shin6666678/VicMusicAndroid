package com.shin.vicmusic.feature.common.item

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.icon.PayTypeIcon
import com.shin.vicmusic.feature.common.icon.PlayCountIcon
import com.shin.vicmusic.feature.common.sheet.PlaylistSelectionSheet
import com.shin.vicmusic.feature.common.sheet.SongActionSheet
import com.shin.vicmusic.feature.playlist.meList.PlaylistViewModel

// 1. 对外使用的有状态组件 (Stateful)
@OptIn(UnstableApi::class)
@Composable
fun ItemSong(
    song: Song,
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel? = null,
    showPlayCount: Boolean = false,
    showDeleteFromPlaylist: Boolean = false,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    if (LocalInspectionMode.current) {
        ItemSongContent(
            song = song,
            modifier = modifier,
            onPlayClick = {},
            onLikeClick = {},
            onMoreClick = {},
            showPlayCount = showPlayCount
        )
        return
    }

    // 正常模式：获取 ViewModel (如果参数为null则注入) 和 CompositionLocal
    val actualViewModel = viewModel ?: hiltViewModel()
    val playerManager = LocalPlayerManager.current
    val actionManager = LocalSongActionManager.current

    // 状态管理：控制两个弹窗的显示
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var showMoreSheet by remember { mutableStateOf(false) }

    val playlists by actualViewModel.playlists.collectAsState()

    LaunchedEffect(Unit) {
        if (playlists.isEmpty()) {
            actualViewModel.fetchMyPlaylists()
        }
    }

    // 更多操作弹窗逻辑
    if (showMoreSheet) {
        SongActionSheet(
            song = song,
            onDismissRequest = { showMoreSheet = false },
            onAddToQueueClick = {
                playerManager.addSongToQueue(song)
                showMoreSheet = false
            },
            onAddToPlaylistClick = {
                showMoreSheet = false
                showPlaylistSheet = true
            },
            showDeleteFromPlaylist = showDeleteFromPlaylist,
            onDeleteClick = {
                showMoreSheet = false
                onDeleteClick()
            },

            )
    }

    // 歌单选择弹窗逻辑
    if (showPlaylistSheet) {
        PlaylistSelectionSheet(
            playlists = playlists,
            onDismissRequest = { showPlaylistSheet = false },
            onPlaylistSelected = { playlist ->
                actualViewModel.addSongToPlaylist(playlist.id, song.id)
            }
        )
    }

    // C. 核心列表项 UI (纯展示)
    ItemSongContent(
        song = song,
        modifier = modifier,
        onPlayClick = { playerManager.playSong(song) },
        onLikeClick = { actionManager.toggleLike(song) },
        // 点击"更多"图标时，只负责改变状态
        onMoreClick = { showMoreSheet = true },
        showPlayCount = showPlayCount,
        onClick = onClick
    )
}

@Composable
fun ItemSongContent(
    song: Song,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    showPlayCount: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onPlayClick(); onClick() },
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
                color = if (song.isCopyright) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${song.artist.name} - ${song.album.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (song.isCopyright) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                PayTypeIcon(song = song)
                if (showPlayCount)
                    PlayCountIcon(playCount = song.playCount)
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
            // 点击触发回调，由父组件决定怎么处理
            IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.MoreVert,
                    "更多",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// 5. 辅助组件 (Helper Composable)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreView() {
    val testSong = Song(
        id = "test_id",
        title = "测试歌曲",
        uri = "",
        icon = "",
        album = Album("1", "测试专辑"),
        artist = Artist("1", "测试歌手"),
        payType = PayType.VIP,
        genre = "Pop",
        lyricStyle = 0,
        lyric = "",
        playCount = 5
    )
    VicMusicTheme {
        // 现在可以直接预览 ItemSong 了
        ItemSong(
            song = testSong,
            showPlayCount = true
        )
    }
}