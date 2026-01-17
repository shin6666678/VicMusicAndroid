package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SongActionButtons(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "喜欢",
                tint = if (isLiked) Color.Red else Color.White
            )
        }
        IconButton(onClick = onCommentClick) {
            Icon(
                imageVector = Icons.Default.Comment,
                contentDescription = "评论",
                tint = Color.White
            )
            //Text("721", fontSize = 10.sp, modifier = Modifier.offset(x = (-8).dp, y = (-8).dp))
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "下载",
                tint = Color.White
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.White
            )
        }
    }
}