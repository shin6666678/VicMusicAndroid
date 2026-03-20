package com.shin.vicmusic.feature.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.UniversalTopBar
import com.shin.vicmusic.feature.feed.component.FeedItemCard
import com.shin.vicmusic.feature.feed.component.FollowingHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    discoveryItems: List<Feed>,
    followingItems: List<Feed>,
    currentUser: UserInfo?,
    pagerState: PagerState,
    followingListState: LazyListState,
    topBarContainerColor: Color,
    topBarSelectTabs: List<BarTabItem>,
    topBarSelectActions: List<BarActionItem>,
    onProfileClick: (String) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onLikeClick: (Feed) -> Unit,
    onCommentClick: (String) -> Unit,
    onBgClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
            when (it) {
                0 -> {
                    if (discoveryItems.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "暂不开放大社区功能",
                                color = LocalAppColors.current.textColor.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "可前往顶部关注界面查看动态",
                                color = LocalAppColors.current.textColor.copy(alpha = 0.5f)
                            )
                        }
                    } else
                        FeedList(
                            items = discoveryItems, onProfileClick, onSongClick, onPlaylistClick,
                            onAlbumClick, onLikeClick, onCommentClick
                        )
                }

                1 -> {
                    LazyColumn(
                        state = followingListState, // Apply the state here
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Add the FollowingHeader at the top
                        if (currentUser != null) {
                            item {
                                FollowingHeader(
                                    user = currentUser,
                                    onBgClick = onBgClick
                                )
                            }
                        }

                        // 2. Add the feed items
                        items(
                            items = followingItems,
                            key = { feed -> feed.id }
                        ) { feed ->
                            FeedItemCard(
                                feed = feed,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onProfileClick = onProfileClick,
                                onSongClick = onSongClick,
                                onPlaylistClick = onPlaylistClick,
                                onAlbumClick = onAlbumClick,
                                onLikeClick = onLikeClick,
                                onCommentClick = onCommentClick,
                            )
                        }
                    }
                }
            }
        }

        // The TopAppBar is now the top layer, with a dynamic background color
        UniversalTopBar(
            tabs = topBarSelectTabs,
            actions = topBarSelectActions,
            backgroundColor = topBarContainerColor
        )
    }

}

@Composable
private fun FeedList(
    items: List<Feed>,
    onProfileClick: (String) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onLikeClick: (Feed) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 80.dp,
            bottom = 8.dp
        ), // Add top padding to not be obscured by TopAppBar
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
            )
        }
    }
}
