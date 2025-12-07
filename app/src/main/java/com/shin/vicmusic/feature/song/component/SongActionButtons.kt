package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.R

@Composable
fun SongActionButtons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO */ }) { // 评论
            Icon(painterResource(id = R.drawable.ic_video_comment), contentDescription = "评论") // 假设有评论图标
            Text("721", fontSize = 10.sp, modifier = Modifier.offset(x = (-8).dp, y = (-8).dp)) // 示例评论数
        }
        IconButton(onClick = { /* TODO */ }) { // 下载
            Icon(painterResource(id = R.drawable.ic_download), contentDescription = "下载") // 假设有下载图标
        }
        IconButton(onClick = { /* TODO */ }) { // 分享
            Icon(Icons.Default.Share, contentDescription = "分享")
        }
        IconButton(onClick = { /* TODO */ }) { // 更多
            Icon(Icons.Default.MoreVert, contentDescription = "更多")
        }
    }
}