package com.shin.vicmusic.feature.main

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState // 导入 currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shin.vicmusic.core.design.component.MyNavigationBar
import com.shin.vicmusic.core.design.component.SongBar
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.player.PlayerState
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.auth.navigateToLogin
import com.shin.vicmusic.feature.discovery.DiscoveryRoute
import com.shin.vicmusic.feature.feed.FeedRoute
import com.shin.vicmusic.feature.me.MeRoute
import com.shin.vicmusic.feature.player.PlayerManager
import com.shin.vicmusic.feature.search.SEARCH_ROUTE // 导入 SEARCH_ROUTE
import com.shin.vicmusic.feature.shortVideo.ShortVideoRoute
import com.shin.vicmusic.feature.song.PlaybackQueueSheet
import com.shin.vicmusic.feature.song.navigateToSongDetail
import kotlinx.coroutines.launch


@Preview
@Composable
fun MainPreView(){
    val previewNavController = rememberNavController()
    MainScreen(
        finishPage = { Log.d("Preview", "Finish page clicked") },
        navController = previewNavController,
        currentPlayingSong = SONG,
        playerState = PlayerState(isPlaying = true, duration = 180000, currentPosition = 60000),
        onSongBarClick = { Log.d("Preview", "SongBar clicked") },
        onSongBarTogglePlayPause = { Log.d("Preview", "Toggle Play/Pause clicked") },
        onSongBarLikeClick = { Log.d("Preview", "Like clicked") },
        onSongBarPlaylistClick = { Log.d("Preview", "Playlist clicked") },
        onAvatarClick = {},
        showNavigationBar = true
    )
}

@Composable
fun MainRoute(
    finishPage:()-> Unit,
    navController:NavHostController,
    viewModel: MainViewModel=hiltViewModel(),
){
    // 观察当前播放的歌曲
    val currentPlayingSong by viewModel.currentPlayingSong.collectAsState()
    // 观察播放器状态
    val playerState by viewModel.playerState.collectAsState()

    // ⭐ 1. 播放队列和索引的观察
    val playbackQueue by viewModel.playbackQueue.collectAsState()
    val currentQueueIndex by viewModel.currentQueueIndex.collectAsState()

    // ⭐ 2. 底部抽屉的显示状态
    var showQueueSheet by remember { mutableStateOf(false) }

    // 监听导航回退栈的变化
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 判断是否显示底部导航栏
    val showNavigationBar = currentRoute != SEARCH_ROUTE

    Box(modifier = Modifier.fillMaxSize()) {

        MainScreen(
            finishPage = finishPage,
            navController = navController,
            currentPlayingSong = currentPlayingSong,
            playerState = playerState,
            onSongBarClick = {
                currentPlayingSong?.let { song ->
                    navController.navigateToSongDetail(song.id)
                }
            },
            onSongBarTogglePlayPause = viewModel::togglePlayPause,
            onSongBarLikeClick = { /* TODO: 实现点赞逻辑 */ },
            onSongBarPlaylistClick = { showQueueSheet = true },
            onAvatarClick = { navController.navigateToLogin() },
            showNavigationBar = showNavigationBar // 传递给 MainScreen
        )

        if (showQueueSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showQueueSheet = false }
            ) {
                PlaybackQueueSheet(
                    queue = playbackQueue,
                    currentIndex = currentQueueIndex,
                    onSongClick = { index ->
                        viewModel.playSongAtIndex(index)
                        showQueueSheet = false
                    },
                    onRemoveSong = { index ->
                        viewModel.removeSong(index)
                    },
                    onClose = { showQueueSheet = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    finishPage: () -> Unit={},
    navController:NavHostController,
    currentPlayingSong: Song?,
    playerState: PlayerState,
    onSongBarClick: () -> Unit,
    onSongBarTogglePlayPause: () -> Unit,
    onSongBarLikeClick: () -> Unit,
    onSongBarPlaylistClick: () -> Unit,
    onAvatarClick: () -> Unit,
    showNavigationBar: Boolean // 新增参数
){
    var currentDestination  by  rememberSaveable {
        mutableStateOf(TopLevelDestination.DISCOVERY.route)
    }
    val pagerState= rememberPagerState{
        4
    }
    val scope= rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            userScrollEnabled = false,
            beyondViewportPageCount = 4
        ) {page->
            when(page){
                0->DiscoveryRoute(navController)
                1->ShortVideoRoute()
                2->MeRoute(onAvatarClick = onAvatarClick)
                3->FeedRoute()
            }
        }

        // SongBar 和 NavigationBar 的容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalDividerColor.current)
        ) {
            // 仅当 showNavigationBar 为 true 时显示底部导航栏
            if (showNavigationBar) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = 30.dp)
                        .navigationBarsPadding()
                ) {
                    MyNavigationBar(
                        destinations = TopLevelDestination.entries,
                        currentDestination = currentDestination,
                        onNavigateToDestination = {index->
                            currentDestination= TopLevelDestination.entries[index].route
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        modifier = Modifier
                    )
                }
            }

            // 播放栏 SongBar (悬浮在顶部并向上偏移)
            currentPlayingSong?.let { song ->
                SongBar(
                    song = song,
                    playerState = playerState,
                    onTogglePlayPause = onSongBarTogglePlayPause,
                    onLikeClick = onSongBarLikeClick,
                    onPlaylistClick = onSongBarPlaylistClick,
                    onBarClick = onSongBarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(0, -placeable.height / 2)
                            }
                        }
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}
