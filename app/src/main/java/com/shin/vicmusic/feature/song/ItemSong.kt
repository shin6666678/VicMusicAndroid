package com.shin.vicmusic.feature.song

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.component.MyAsyncImage
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceSmallHeight
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.Song // [修改] 引用 Domain Model
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG

@Composable
fun ItemSong(
    data: Song,
    modifier: Modifier= Modifier,
    onAddToQueueClick: (Song) -> Unit = {},
    onLikeClick:(Song)->Unit={}
) {
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
                // [修改] Domain Model 中 artist 和 album 是对象，需要取其 name/title
                text = "${data.artist.name} - ${data.album.title}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = {onLikeClick(data)}
        ) {
            Icon(
                imageVector = if(data.isLiked)Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "喜欢",
                // 喜欢状态为红色，否则为默认色
                tint = if (data.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        // ⭐ 操作按钮：添加到队列
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