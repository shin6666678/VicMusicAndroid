package com.shin.vicmusic.feature.song

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.component.MyAsyncImage
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceSmallHeight
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG

@Composable
fun ItemSong(data: Song, modifier: Modifier= Modifier,onAddToQueueClick: (Song) -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyAsyncImage(model = data.icon, modifier = Modifier.size(50.dp))
        Column(
            modifier= Modifier.weight(1f).padding(horizontal = SpaceMedium)
        ) {
            Text(
                text = data.title,
                //overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            SpaceSmallHeight()
            Text(
                text = "${data.artist} - ${data.album}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        // ⭐ 新增操作按钮：添加到队列
        IconButton(
            onClick = { onAddToQueueClick(data) } // 调用回调，传递当前歌曲
        ) {
            Icon(
                imageVector = Icons.Default.Add, // 使用加号图标
                contentDescription = "添加到队列",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
@Preview
@Composable
fun PreView(){
    VicMusicTheme {
        ItemSong(
            data=SONG
        )
    }
}