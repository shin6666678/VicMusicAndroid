package com.shin.vicmusic.feature.common.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.common.BottomSheetActionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongActionSheet(
    song: Song,
    onDismissRequest: () -> Unit={},
    onAddToQueueClick: () -> Unit={},
    onAddToPlaylistClick: () -> Unit={},
    onDeleteClick: () -> Unit={},
    showDeleteFromPlaylist: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // 顶部信息
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyAsyncImage(
                    model = song.icon,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        " ${song.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        song.artist.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            // 功能网格
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomSheetActionItem(
                    Icons.Default.PlayArrow, "下一首播放",
                    modifier = Modifier.clickable {
                        onAddToQueueClick()
                        onDismissRequest()
                    })
                BottomSheetActionItem(Icons.Default.FavoriteBorder, "收藏")
                BottomSheetActionItem(
                    Icons.Default.Add, "加入歌单",
                    modifier = Modifier.clickable {
                        // 这里不做任何状态变更，只调用回调，控制权交给父组件
                        onAddToPlaylistClick()
                    })
                BottomSheetActionItem(Icons.Default.Share, "分享")
                BottomSheetActionItem(Icons.Default.ArrowDropDown, "下载")
            }

            // 列表选项
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "单曲购买",
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth()
                )
                if (showDeleteFromPlaylist) {
                    Text(
                        "删除",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeleteClick() }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}