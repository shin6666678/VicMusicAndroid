package com.shin.vicmusic.feature.playlist.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
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
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.common.BottomSheetActionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistShareActionSheet(
    playlist: Playlist,
    onDismissRequest: () -> Unit,
    onShareToFeedClick: () -> Unit,
    onGenerateCardClick: () -> Unit,
    onSystemShareClick: () -> Unit
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
                    model = playlist.cover,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "分享歌单",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        playlist.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                    Icons.Default.Send, "分享到动态",
                    modifier = Modifier.clickable {
                        onShareToFeedClick()
                        onDismissRequest()
                    })
                BottomSheetActionItem(
                    Icons.Default.Image, "生成海报",
                    modifier = Modifier.clickable {
                        onGenerateCardClick()
                        onDismissRequest()
                    })
                BottomSheetActionItem(
                    Icons.Default.Share, "系统分享",
                    modifier = Modifier.clickable {
                        onSystemShareClick()
                        onDismissRequest()
                    })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
