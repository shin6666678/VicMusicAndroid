package com.shin.vicmusic.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist

@Preview
@Composable
fun ItemAlbumPreview() {
    ItemAlbum(
        album = Album(
            id = "1",
            title = " album title",
            artist = Artist(id = "1", name = "artist name"),
            icon = "https://picsum.photos/200/300",
            description = "description",
            company = "company",
            releaseTime = "2023-05-01",
            style = "style",
            songCount = 10,
            isLiked = false
        ),
        onClick = {}
    )
}

@Composable
fun ItemAlbum(
    album: Album,
    onClick: () -> Unit
) {
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
            if (album.icon.isEmpty()) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.MusicNote),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                MyAsyncImage(
                    model = album.icon,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${album.songCount}首 来自 ${album.artistName}",
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
fun ItemAlbumSquare(
    album: Album,
    onAlbumClick: (String) -> Unit = {}
){
    Column(modifier = Modifier
        .padding(8.dp)
        .clickable { onAlbumClick(album.id) }
    ) {
        MyAsyncImage(
            model = album.icon,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f), // 建议给图片指定固定高度或宽高比
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = album.title)
    }
}