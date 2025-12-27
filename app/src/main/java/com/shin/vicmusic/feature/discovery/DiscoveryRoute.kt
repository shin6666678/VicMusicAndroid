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
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.feature.album.albumList.ALBUM_LIST_ROUTE
import com.shin.vicmusic.feature.discovery.musicHall.MusicHall
import com.shin.vicmusic.feature.discovery.recommend.RecommendRoute
import com.shin.vicmusic.feature.discovery.recommend.RecommendScreen
import com.shin.vicmusic.feature.rankList.rankList.RANK_LIST_ROUTE
import kotlinx.coroutines.launch


@Composable
fun DiscoveryRoute(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel(),
) {
    val playerManager = LocalPlayerManager.current
    val datum by viewModel.datum.collectAsState()
    val user by viewModel.user.collectAsState() // 用户状态
    val alsoListeningCard by viewModel.alsoListeningCard.collectAsState()

    DiscoveryScreen(
        songs = datum,
        user=user,
        alsoListeningCard = alsoListeningCard,
        toSearch = { navController.navigate("search_route") }, // 点击搜索框时导航到搜索界面
        onQuickAccessClick = { label ->
            if (label == "歌手") {
                navController.navigate("artist_list")
            }else if(label=="排行"){
                navController.navigate(RANK_LIST_ROUTE)
            }else if(label=="专辑"){
                navController.navigate(ALBUM_LIST_ROUTE)
            }
        }
    )
}

@Composable
fun DiscoveryScreen(
    toSearch: () -> Unit = {},
    songs: List<Song> = listOf(),
    user: UserInfo?=null,
    alsoListeningCard: RecommendCard = RecommendCard(title = "", songs = emptyList()),
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
    songs:List<Song>
) {
    DiscoveryScreen(songs = songs)
}