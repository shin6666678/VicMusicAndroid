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
    onLikeClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [新增] 喜欢按钮
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "喜欢",
                tint = if (isLiked) Color.Red else Color.White
            )
        }
        IconButton(onClick = { /* TODO */ }) { // 评论
            // 使用 Icons.Default.Comment 替换 painterResource
            Icon(Icons.Default.Comment, contentDescription = "评论")
            Text("721", fontSize = 10.sp, modifier = Modifier.offset(x = (-8).dp, y = (-8).dp))
        }
        IconButton(onClick = { /* TODO */ }) { // 下载
            // 使用 Icons.Default.Download 替换 painterResource
            Icon(Icons.Default.Download, contentDescription = "下载")
        }
        IconButton(onClick = { /* TODO */ }) { // 分享
            Icon(Icons.Default.Share, contentDescription = "分享")
        }
        IconButton(onClick = { /* TODO */ }) { // 更多
            Icon(Icons.Default.MoreVert, contentDescription = "更多")
        }
    }
}