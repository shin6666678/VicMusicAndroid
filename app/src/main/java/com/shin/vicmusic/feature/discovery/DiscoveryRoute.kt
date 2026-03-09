package com.shin.vicmusic.feature.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.design.theme.AppBackground
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.album.albumList.ALBUM_LIST_ROUTE
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.SearchBar
import com.shin.vicmusic.feature.common.bar.UniversalTopBar
import com.shin.vicmusic.feature.discovery.musicHall.MusicHall
import com.shin.vicmusic.feature.discovery.recommend.RecommendRoute
import com.shin.vicmusic.feature.myInfo.navigateToMyInfo
import com.shin.vicmusic.feature.playlist.publicList.PUBLIC_PLAYLISTS_ROUTE
import com.shin.vicmusic.feature.rankList.rankList.RANK_LIST_ROUTE
import com.shin.vicmusic.feature.search.navigateToSearch
import kotlinx.coroutines.launch

@Composable
fun DiscoveryRoute(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel(), // 这里的 VM 仅为了 MusicHall 暂时保留
) {
    // MusicHall 尚未重构，所以还在外层获取
    val datum by viewModel.datum.collectAsState()

    DiscoveryScreen(
        songs = datum,
        toSearch = { navController.navigateToSearch() },
        toMe = { navController.navigateToMyInfo() },
        onQuickAccessClick = { label ->
            when (label) {
                "歌手" -> navController.navigate("artist_list")
                "排行" -> navController.navigate(RANK_LIST_ROUTE)
                "专辑" -> navController.navigate(ALBUM_LIST_ROUTE)
                "歌单" -> navController.navigate(PUBLIC_PLAYLISTS_ROUTE)
            }
        }
    )
}

@Composable
fun DiscoveryScreen(
    toSearch: () -> Unit = {},
    toMe: () -> Unit = {},
    songs: List<Song> = listOf(),
    onQuickAccessClick: (String) -> Unit = {}
) {
    val tabTitles = listOf("推荐", "乐馆")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    val topBarTabs = tabTitles.mapIndexed { index, title ->
        BarTabItem(
            name = title,
            isSelected = pagerState.currentPage == index,
            onClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                UniversalTopBar(
                    tabs = topBarTabs,
                )
                SearchBar(
                    toSearch = toSearch,
                    toMe=toMe
                )
            }
        },
        containerColor = Color.Transparent,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars)
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> {
                    RecommendRoute()
                }

                1 -> {
                    MusicHall(
                        songs = songs,
                        onQuickAccessClick = onQuickAccessClick
                    )
                }
            }

        }
    }
}