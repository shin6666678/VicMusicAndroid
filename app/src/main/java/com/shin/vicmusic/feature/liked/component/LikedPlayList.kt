package com.shin.vicmusic.feature.liked.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.common.item.ItemPlaylist
import com.shin.vicmusic.feature.common.bar.CommonSearchBar

@Composable
fun LikedPlayList(
    playLists: List<Playlist>,
    onClick: (String) -> Unit
) {
    Column() {
        CommonSearchBar(
            toSearch = {},
            placeHolderString = "搜索我收藏的歌单",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFE0FFEE), RoundedCornerShape(50)) // 绿色背景
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF00C853)
                ) // 绿色图标
                Spacer(Modifier.width(8.dp))
                Text("${playLists.size}张歌单", color = Color.Black)
            }
            Row {
                IconButton(onClick = { /*TODO: Filter action*/ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "筛选")
                }
                IconButton(onClick = { /*TODO: Sort action*/ }) {
                    Icon(Icons.Default.Sort, contentDescription = "排序")
                }
            }
        }

        LazyColumn {
            items(playLists) { playlist ->
                ItemPlaylist(
                    playlist = playlist,
                    onClick = { onClick(playlist.id) }
                )
            }
        }
    }
}