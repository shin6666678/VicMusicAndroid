package com.shin.vicmusic.feature.relationship

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
import com.shin.vicmusic.feature.relationship.fanList.FanListScreen
import com.shin.vicmusic.feature.relationship.followList.FollowListScreen
import com.shin.vicmusic.feature.relationship.friendList.FriendListScreen
import kotlinx.coroutines.launch


@Composable
fun RelationShipRoute(
    initialTab: RelationshipTab,
) {
    val navController= LocalNavController.current
    RelationshipScreen(
        initialTab = initialTab,
        onBackClick = {
            navController.popBackStack()
        }
    )
}

@Composable
fun RelationshipScreen(
    initialTab: RelationshipTab,
    onBackClick: () -> Unit
) {
    val tabTitles = listOf("关注", "粉丝", "好友")

    val pagerState = rememberPagerState(
        initialPage = initialTab.ordinal,
        pageCount = { tabTitles.size }
    )
    val coroutineScope = rememberCoroutineScope()

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
            CommonTopBarSelect(
                tabs = topBarTabs,
                backgroundColor = MaterialTheme.colorScheme.surface,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> FollowListScreen()
                1 -> FanListScreen()
                2 -> FriendListScreen()
            }
        }
    }
}

@Preview
@Composable
fun RelationshipScreenPreview() {
    RelationshipScreen(
        initialTab = RelationshipTab.FOLLOWING,
        onBackClick = {}
    )
}