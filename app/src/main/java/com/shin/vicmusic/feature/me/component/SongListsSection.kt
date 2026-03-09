package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.common.CreatePlaylistDialog
import com.shin.vicmusic.feature.common.item.ItemPlaylist
import com.shin.vicmusic.core.design.theme.LocalAppColors

@Composable
fun PlaylistsSection(
    myPlaylists: List<Playlist> = emptyList(),
    likedPlayLists: List<Playlist> = emptyList(),
    onPlaylistClick: (String) -> Unit = {},
    onMorePlaylistsClick: () -> Unit = {}
) {
    // 1. 引入状态：0 代表“我的歌单”，1 代表“收藏歌单”
    var selectedTabIndex by remember { mutableStateOf(0) }

    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
        )
    }

    val textColor = LocalAppColors.current.textColor
    val activeColor = Color(0xFF00BFA5) // 选中的高亮色

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tab 头部区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            // 我的歌单 Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selectedTabIndex = 0 }
            ) {
                Text(
                    text = "我的歌单",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 0) textColor else textColor.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Divider(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(if (selectedTabIndex == 0) activeColor else Color.Transparent)
                )
            }

            Spacer(Modifier.width(16.dp))

            // 收藏歌单 Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selectedTabIndex = 1 }
            ) {
                Text(
                    text = "收藏歌单",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 1) textColor else textColor.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Divider(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(if (selectedTabIndex == 1) activeColor else Color.Transparent)
                )
            }

            Spacer(Modifier.weight(1f))

            // 操作图标
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Playlist",
                tint = textColor,
                modifier = Modifier.clickable { showCreateDialog = true }
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "More Playlists",
                tint = textColor,
                modifier = Modifier.clickable { onMorePlaylistsClick() }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 2. 根据选中的索引切换数据源 (Data Source)
        val displayPlaylists = if (selectedTabIndex == 0) myPlaylists else likedPlayLists

        // 动态显示歌单列表
        displayPlaylists.take(3).forEach { playlist ->
            ItemPlaylist(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 如果列表为空，可以增加一个空状态占位 (Optional/可选)
        if (displayPlaylists.isEmpty()) {
            Text(
                text = "暂无歌单",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}