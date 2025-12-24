package com.shin.vicmusic.feature.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController // 导入 NavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterProvider
import androidx.compose.runtime.rememberCoroutineScope
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.feature.album.albumList.ALBUM_LIST_ROUTE
import com.shin.vicmusic.feature.discovery.musicHall.MusicHall
import com.shin.vicmusic.feature.discovery.recommend.Recommend
import com.shin.vicmusic.feature.rankList.rankList.RANK_LIST_ROUTE
import kotlinx.coroutines.launch


@Composable
fun DiscoveryRoute(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel(),
) {
    val playerManager = LocalPlayerManager.current
    val datum by viewModel.datum.collectAsState()
    val user by viewModel.user.collectAsState() // [新增] 收集用户状态

    DiscoveryScreen(
        songs = datum,
        user=user,
        toggleDrawer = {}, // 保持原有的空实现，或者替换为实际逻辑
        toSearch = { navController.navigate("search_route") }, // 点击搜索框时导航到搜索界面
        onSongClick = { songId -> playerManager::playSong} ,
        onAddToQueueClick = { song -> playerManager.addSongToQueue(song) },
        // [新增] 传递 ViewModel 的 toggleLike 方法
        onLikeClick = viewModel::toggleLike,
        onQuickAccessClick = { label ->
            if (label == "歌手") {
                navController.navigate("artist_list")
            }else if(label=="排行"){
                navController.navigate(RANK_LIST_ROUTE)
            }else if(label=="专辑"){
                navController.navigate(ALBUM_LIST_ROUTE)
            }
            // 可以繼續處理其他點擊，例如 "每日推薦"
        }
    )
}

@Composable
fun DiscoveryScreen(
    toggleDrawer: () -> Unit = {},
    toSearch: () -> Unit = {},
    songs: List<Song> = listOf(),
    user: User?=null,
    onSongClick: (String) -> Unit = {} ,
    onAddToQueueClick: (Song) -> Unit = {},
    // [新增] 接收点击回调
    onLikeClick: (Song) -> Unit = {},
    onQuickAccessClick: (String) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = {3})
    val coroutineScope= rememberCoroutineScope()
    Scaffold(
        topBar = {
            DiscoveryTopBar(
                toSearch = toSearch,
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(paddingValues) // 保持原有的 padding 以避免被 TopBar 遮挡

        ) { page ->
            // 6. 根据 page 索引渲染对应的内容
            when (page) {
                0 -> Recommend(user=user)
                1 -> MusicHall(
                    songs = songs,
                    onQuickAccessClick = onQuickAccessClick
                )

            }
        }
    }
}

@Preview
@Composable
fun PreView(
    @PreviewParameter(DiscoveryPreviewParameterProvider::class)
    songs:List<Song>
) {
    DiscoveryScreen(songs = songs)
}