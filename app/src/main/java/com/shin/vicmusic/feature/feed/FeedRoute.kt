package com.shin.vicmusic.feature.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val discoveryItems by viewModel.discoveryItems.collectAsState()
    val followingItems by viewModel.followingItems.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

    val pagerState = rememberPagerState(initialPage = selectedTabIndex) { 2 }

    // 当 ViewModel 中的 Tab 变化时，滚动 Pager
    LaunchedEffect(selectedTabIndex) {
        if (pagerState.currentPage != selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }

    // 当用户滑动 Pager 时，更新 ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (selectedTabIndex != page) {
                viewModel.updateTab(page)
            }
        }
    }

    val commonTopBarSelectTabs = listOf(
        BarTabItem(
            name = "发现",
            isSelected = selectedTabIndex == 0,
            onClick = { viewModel.updateTab(0) },
        ),
        BarTabItem(
            name = "关注",
            isSelected = selectedTabIndex == 1,
            onClick = { viewModel.updateTab(1) },
        ),
    )
    val commonTopBarSelectActions = listOf(
        BarActionItem(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = { /* TODO: Navigate to Search */ },
        ),
        BarActionItem(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = { /* TODO: Navigate to Post creation */ },
        ),
    )

    FeedScreen(
        discoveryItems = discoveryItems,
        followingItems = followingItems,
        pagerState = pagerState,
        topBarSelectTabs = commonTopBarSelectTabs,
        topBarSelectActions = commonTopBarSelectActions,
        onProfileClick = { /* TODO: Navigate to profile screen */ },
        onSongClick = { /* TODO: Navigate to song detail */ },
        onPlaylistClick = { /* TODO: Navigate to playlist detail */ },
        onAlbumClick = { /* TODO: Navigate to album detail */ },
        onLikeClick = { /* TODO: Handle like action */ },
        onCommentClick = { /* TODO: Navigate to comment screen */ },
        onPlayClick = { /* TODO: Handle play action */ },
    )
}
