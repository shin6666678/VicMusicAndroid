package com.shin.vicmusic.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.playlist.detail.navigateToPlaylistDetail

@Composable
fun ItemPlaylist(playlist: Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            if (playlist.cover.isNullOrEmpty()) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.MusicNote),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                MyAsyncImage(
                    model = playlist.cover,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${playlist.playCount}首",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
    }
}

@Composable
fun ItemPlaylistSquare(
    playlist: Playlist,
) {
    val navController= LocalNavController.current
    Column(
        modifier = Modifier
            .width(100.dp) // 限制宽度，适配横向列表
            .clickable(onClick = { navController.navigateToPlaylistDetail(playlist.id) }),
        horizontalAlignment = Alignment.CenterHorizontally // 内容水平居中
    ) {
        Surface(
            modifier = Modifier.size(100.dp), // 图片设为正方形
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            if (playlist.cover.isNullOrEmpty()) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.MusicNote),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                MyAsyncImage(
                    model = playlist.cover,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = playlist.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1, // 只显示一行
            overflow = TextOverflow.Ellipsis, // 超出显示省略号
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
