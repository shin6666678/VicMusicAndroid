package com.shin.vicmusic.feature.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.UniversalTopBar
import com.shin.vicmusic.feature.feed.component.FeedItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    discoveryItems: List<Feed>,
    followingItems: List<Feed>,
    pagerState: PagerState,
    topBarSelectTabs: List<BarTabItem>,
    topBarSelectActions: List<BarActionItem>,
    onProfileClick: (String) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onPlayClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            UniversalTopBar(
                tabs = topBarSelectTabs,
                actions = topBarSelectActions
            )
        }
    ) {paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                when (it) {
                    0 -> FeedList(items = discoveryItems, onProfileClick, onSongClick, onPlaylistClick, onAlbumClick, onLikeClick, onCommentClick, onPlayClick)
                    1 -> FeedList(items = followingItems, onProfileClick, onSongClick, onPlaylistClick, onAlbumClick, onLikeClick, onCommentClick, onPlayClick)
                }
            }
        }
    }
}

@Composable
private fun FeedList(
    items: List<Feed>,
    onProfileClick: (String) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onPlayClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { feed -> feed.id }
        ) { feed ->
            FeedItemCard(
                feed = feed,
                modifier = Modifier.padding(horizontal = 8.dp),
                onProfileClick = onProfileClick,
                onSongClick = onSongClick,
                onPlaylistClick = onPlaylistClick,
                onAlbumClick = onAlbumClick,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick,
                onPlayClick = onPlayClick
            )
        }
    }
}