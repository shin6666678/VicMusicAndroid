package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.common.CreatePlaylistDialog

@Composable
fun SongListsSection(
    playlists: List<Playlist> = emptyList(),
    onMorePlaylistsClick: () -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
        )
    }

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
                color = Color.Black
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
                color = Color.Gray
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
            tint = Color.Gray,
            modifier = Modifier.clickable {
                showCreateDialog = true
            }
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "More Playlists",
            tint = Color.Gray,
            modifier = Modifier.clickable { onMorePlaylistsClick() }
        )
    }

    Spacer(Modifier.height(16.dp))

    // 动态显示歌单列表 (最多3条)
    playlists.take(3).forEach { playlist ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playlist.cover.isNullOrEmpty()) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    AsyncImage(
                        model = playlist.cover,
                        contentDescription = "Playlist Cover",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(playlist.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${playlist.playCount}首",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}