package com.shin.vicmusic.feature.discovery.recommend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.common.item.ItemSongContent

@Preview(showBackground = true)
@Composable
fun PreviewAlsoListeningSection() {
    VicMusicTheme {
        AlsoListeningSection(
            title = "听「梁静茹」的也在听",
            songs = SONGS,
        )
    }
}

@Composable
fun AlsoListeningSection(
    title: String,
    songs: List<Song>,
) {
    // 1. 数据处理：只取前6首 (2列 * 3行)，然后每3首分为一组
    // 结果结构: [[Song1, Song2, Song3], [Song4, Song5, Song6]]
    val songColumns = songs.take(6).chunked(3)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceOuter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(SpaceMedium))

        // 内容区：横向滚动
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = SpaceOuter),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // 列与列之间的间距
        ) {
            // 遍历每一组 (每一组包含3首歌)
            items(songColumns) { columnSongs ->
                // 每一列是一个垂直布局
                Column(
                    // 设置宽度占父容器宽度的 90% (或其他比例)，让屏幕主要展示这一列
                    modifier = Modifier.fillParentMaxWidth(0.93f),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // 同一列中，歌曲与歌曲的垂直间距
                ) {
                    columnSongs.forEach { song ->
                        ItemSongContent(song = song)
                    }
                }
            }
        }
    }
}