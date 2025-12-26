package com.shin.vicmusic.feature.artist.artistDetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.PlayerManager
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.ARTIST
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistMainCard
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistTabRow
import com.shin.vicmusic.feature.artist.artistDetail.component.ArtistTopBar
import com.shin.vicmusic.feature.common.ItemSong

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
) {
    val tabs = listOf("百科", "歌曲", "专辑")
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    // 计算关键高度
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topBarHeight = 56.dp + statusBarHeight
    val imageHeight = 340.dp

    // 转换为像素进行计算
    val topBarHeightPx = with(density) { topBarHeight.toPx() }
    val imageHeightPx = with(density) { imageHeight.toPx() }
    // 触发吸顶和变色的阈值：图片高度 - TopBar高度
    val triggerPx = imageHeightPx - topBarHeightPx

    val outerState = rememberLazyListState()

    // 计算 TopBar 背景透明度
    val topBarAlpha by remember {
        derivedStateOf {
            if (outerState.firstVisibleItemIndex > 0) 1f
            else (outerState.firstVisibleItemScrollOffset / triggerPx).coerceIn(0f, 1f)
        }
    }

    // 判断是否显示悬浮的 Tab 栏
    val showStickyTabs by remember {
        derivedStateOf {
            outerState.firstVisibleItemIndex > 0 || outerState.firstVisibleItemScrollOffset >= triggerPx
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) { // 手指上推
                    val consumed = outerState.dispatchRawDelta(-available.y)
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0) { // 手指下拉
                    val consumedY = outerState.dispatchRawDelta(-available.y)
                    return Offset(0f, -consumedY)
                }
                return Offset.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = outerState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // 0. 头部图片
            item {
                ArtistMainCard(
                    artist = artist,
                    onFollowClick = onFollowClick,
                    onPlayHotSongsClick = onPlayHotSongsClick,
                    modifier = Modifier
                )
            }

            // 1. Tab 栏 (列表内的占位，滚动时会随列表移动)
            item {
                ArtistTabRow(
                    tabs = tabs,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope
                )
            }

            // 2. 内容 Pager
            item {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .height(screenHeight)
                        .nestedScroll(nestedScrollConnection)
                ) { page ->
                    when (page) {
                        1 -> if (songs != null) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(songs) { song ->
                                    ItemSong(
                                        song = song,
                                        modifier = Modifier.clickable { playerManager?.playSong(song) },
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        // --- 悬浮层 (Overlay) ---

        // 1. 顶部 TopBar (固定在顶部，背景色动态变化)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // 使用 surfaceVariant 或其他你喜欢的背景色，并应用 alpha
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = topBarAlpha))
                .zIndex(2f)
        ) {
            ArtistTopBar(onBackClick = onBackClick)
        }

        // 2. 吸顶的 Tab 栏 (仅在列表滚动超过阈值时显示，位置在 TopBar 下方)
        if (showStickyTabs) {
            Box(
                modifier = Modifier
                    .padding(top = topBarHeight) // 关键：位置偏移到 TopBar 下方
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                ArtistTabRow(
                    tabs = tabs,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope
                )
            }
        }
    }
}
