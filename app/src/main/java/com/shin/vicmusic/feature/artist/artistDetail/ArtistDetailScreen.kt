package com.shin.vicmusic.feature.artist.artistDetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.usecase.CheckVipPermissionUseCase
import com.shin.vicmusic.core.manager.PlaybackQueueManager
import com.shin.vicmusic.core.manager.PlayerManager
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.ARTIST
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistMainCard
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistSongList
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistTopBar
import com.shin.vicmusic.feature.song.ItemSong
import kotlinx.coroutines.launch

@Preview
@Composable
fun ArtistDetailScreenPreview() {
    ArtistDetailScreen(
        artist = ARTIST,
        songs = SONGS,
        onPlayHotSongsClick = {},
        onFollowClick = {},
        playerManager = null,
        onBackClick = {}
    )
}

@Composable
fun ArtistDetailRoute(
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    val playerManager = LocalPlayerManager.current

    val artist by viewModel.artist.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val navController = LocalNavController.current
    artist?.let {
        ArtistDetailScreen(
            artist = it,
            songs = songs,
            playerManager = playerManager,
            onBackClick = navController::popBackStack,
            onPlayHotSongsClick = { /* TODO: Implement play hot songs logic */ },
            onFollowClick = { artistId -> /* TODO: Implement follow logic */ }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistDetailScreen(
    artist: Artist,
    songs: List<Song>?,
    playerManager: PlayerManager? = null,
    onBackClick: () -> Unit = {},
    onFollowClick: (String) -> Unit = {},
    onPlayHotSongsClick: () -> Unit = {},
    onLikeClick: (Song) -> Unit = {}
) {
    val tabs = listOf("百科", "歌曲", "专辑")
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    // 获取屏幕高度，赋值给 Pager 确保其能正确测量
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 1. 创建外部列表状态
    val outerState = rememberLazyListState()

    // 2. 创建连接
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // 下滑时 (手指上推, available.y < 0): 外部优先消费 (隐藏 Header)
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    val consumed = outerState.dispatchRawDelta(-available.y)
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            // 上滑时 (手指下拉, available.y > 0): 内部先消费，剩下来的外部再消费 (显示 Header)
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0) {
                    val consumedY = outerState.dispatchRawDelta(-available.y)
                    return Offset(0f, -consumedY)
                }
                return Offset.Zero
            }
        }
    }

    LazyColumn(
        state = outerState, // 3. 绑定状态
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        item { ArtistTopBar(onBackClick = onBackClick) }

        item {
            ArtistMainCard(
                artist = artist,
                onFollowClick = onFollowClick,
                onPlayHotSongsClick = onPlayHotSongsClick,
                modifier = Modifier
            )
        }

        stickyHeader { // 使用 stickyHeader 让 Tab 栏吸顶
            Column( // 给 Tab栏添加背景色防止透明重叠
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = SpaceOuter),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(tabs) { index, tabText ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = tabText,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                }
                            )
                            if (pagerState.currentPage == index) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .width(24.dp)
                                        .height(2.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(screenHeight) // 必须设置高度为屏幕高度
                    .nestedScroll(nestedScrollConnection) // 4. 绑定连接
            ) { page ->
                when (page) {
                    1 -> if (songs != null) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(songs) { song ->
                                ItemSong(
                                    song = song,
                                    modifier = Modifier.clickable { playerManager?.playSong(song) },
                                    onAddToQueueClick = { playerManager?.addSongToQueue(song) },
                                    onLikeClick = onLikeClick
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}