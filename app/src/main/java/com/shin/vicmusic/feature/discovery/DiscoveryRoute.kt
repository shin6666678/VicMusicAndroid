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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterProvider
import com.shin.vicmusic.feature.album.albumList.ALBUM_LIST_ROUTE
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.SearchBar
import com.shin.vicmusic.feature.common.bar.UniversalTopBar
import com.shin.vicmusic.feature.discovery.musicHall.MusicHall
import com.shin.vicmusic.feature.discovery.recommend.RecommendRoute
import com.shin.vicmusic.feature.playlist.publicList.PUBLIC_PLAYLISTS_ROUTE
import com.shin.vicmusic.feature.rankList.rankList.RANK_LIST_ROUTE
import kotlinx.coroutines.launch


@Composable
fun DiscoveryRoute(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel(),
) {
    val datum by viewModel.datum.collectAsState()
    val user by viewModel.user.collectAsState()
    val alsoListeningCard by viewModel.alsoListeningCard.collectAsState()


    DiscoveryScreen(
        songs = datum,
        user = user,
        alsoListeningCard = alsoListeningCard,
        onQuickAccessClick = { label ->
            if (label == "歌手") {
                navController.navigate("artist_list")
            } else if (label == "排行") {
                navController.navigate(RANK_LIST_ROUTE)
            } else if (label == "专辑") {
                navController.navigate(ALBUM_LIST_ROUTE)
            } else if (label == "歌单") {
                navController.navigate(PUBLIC_PLAYLISTS_ROUTE)
            }
        }
    )
}

@Composable
fun DiscoveryScreen(
    toSearch: () -> Unit = {},// [修改] 接收 Actions
    songs: List<Song> = listOf(),
    user: UserInfo? = null,
    alsoListeningCard: RecommendCard = RecommendCard(title = "", songs = emptyList()),
    onQuickAccessClick: (String) -> Unit = {}
) {
    // 定义 Tab 标题
    val tabTitles = listOf("推荐", "乐馆")

    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    // [修改] 2. 在 Screen 内部组装 Tab，因为需要与 pagerState 深度绑定
    val topBarTabs = tabTitles.mapIndexed { index, title ->
        BarTabItem(
            name = title,
            isSelected = pagerState.currentPage == index,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column() {
                UniversalTopBar(
                    tabs = topBarTabs,
                    backgroundColor = MaterialTheme.colorScheme.surface
                )
                SearchBar(toSearch = toSearch)
            }

        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> RecommendRoute(
                    user = user,
                    recommendCard = alsoListeningCard
                )
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
    songs: List<Song>
) {
    DiscoveryScreen(songs = songs)
}