package com.shin.vicmusic.feature.playBackQueue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS

@Preview
@Composable
fun PlaybackQueuePreview() {
    PlaybackQueueSheet(
        isPlayingQueue = SONGS,
        currentIndex = 1,
        onSongClick = {},
        onRemoveSong = {},
        modifier = Modifier,

    )
}

@Composable
fun PlaybackQueueSheet(
    isPlayingQueue: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onRemoveSong: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onClose:()->Unit={},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("正在播放 ${isPlayingQueue.size}", "已播歌曲", "已播歌单")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f) // 占屏幕高度的 60%
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {

        PlayBackQueueTopBar(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabClick = { newIndex -> selectedTab = newIndex }
        )

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        // 2. 内容区域
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> IsPlayingQueue(isPlayingQueue, currentIndex, onSongClick, onRemoveSong)
                1 -> AlreadyPlayedSongQueue()
                2 -> AlreadyPlayedPlayListQueue()
            }
        }
    }
}