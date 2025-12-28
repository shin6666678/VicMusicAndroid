package com.shin.vicmusic.feature.playlist.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.feature.common.BottomSheetActionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaySongActionHeader(
    playListId: String,
    songCount: Int,
    onPlayAllClick: () -> Unit={},
    onChangePublicStatus:(String)->Unit={}
){
    var showSheet by remember { mutableStateOf(false) }

    // 1. 播放全部按钮的部分 (作为一个 item 存在)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.clickable { onPlayAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1DB954)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "全部播放",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "全部播放(${songCount})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            // 点击触发底部弹窗
            IconButton(onClick = { showSheet = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.MoreVert, "更多(More)", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }



    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                // 1. 顶部歌曲信息(Header Info)
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                // 2. 功能图标网格(Action Grid)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomSheetActionItem(Icons.Default.RemoveRedEye, "设为隐私",
                        modifier = Modifier.clickable {
                            showSheet = false
                            onChangePublicStatus(playListId)
                        })
                }
                // 3. 列表选项(List Options)
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("设为隐私", modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth())
                }
            }
        }
    }
}
