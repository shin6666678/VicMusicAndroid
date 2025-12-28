package com.shin.vicmusic.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Album

@Composable
fun ItemAlbum(
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