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
    playlists: List<Playlist> = emptyList(),
    onPlaylistClick: (String) -> Unit = {},
    onMorePlaylistsClick: () -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
        )
    }

    val textColor = LocalAppColors.current.textColor

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { /* Handle tab click */ }
        ) {
            Text(
                text = "自建歌单",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(Modifier.height(4.dp))
            Divider(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color(0xFF00BFA5))
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { /* Handle tab click */ }
        ) {
            Text(
                text = "收藏歌单",
                style = MaterialTheme.typography.titleMedium,
                color = textColor.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Divider(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color.Transparent)
            )
        }
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Playlist",
            tint = textColor,
            modifier = Modifier.clickable {
                showCreateDialog = true
            }
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

    // 动态显示歌单列表 (最多3条)
    playlists.take(3).forEach { playlist ->
        ItemPlaylist(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
        Spacer(modifier = Modifier.height(12.dp))
    }
}