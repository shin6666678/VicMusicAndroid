package com.shin.vicmusic.feature.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.discovery.DiscoveryRoute
import com.shin.vicmusic.feature.feed.FeedRoute
import com.shin.vicmusic.feature.me.MeRoute
import com.shin.vicmusic.feature.shortVideo.ShortVideoRoute


@Preview
@Composable
fun MainPreView(){
    val previewNavController = rememberNavController()
//    MainScreen(
//        navController = previewNavController,
//        onAvatarClick = {},
//        mainTabState = rememberSaveable { mutableIntStateOf(0) }
//    )
}

@Composable
fun MainRoute(
    mainTabState: MutableIntState
){
    val navController = LocalNavController.current

    Box(modifier = Modifier.fillMaxSize()) {

        MainScreen(
            mainTabState = mainTabState,
            pageContent = { page ->
                // 在这里注入真实的路由
                when (page) {
                    0 -> DiscoveryRoute(navController)
                    1 -> ShortVideoRoute()
                    2 -> MeRoute()
                    3 -> FeedRoute()
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    mainTabState: MutableIntState,
    pageContent: @Composable (Int) -> Unit
){

    val pagerState= rememberPagerState{
        4
    }
    // 同步外部 currentTab 到 pagerState
    LaunchedEffect(mainTabState.intValue) {
        if (pagerState.currentPage != mainTabState.intValue) {
            pagerState.scrollToPage(mainTabState.intValue)
        }
    }
    //监听 pagerState 变化并通知外部
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (mainTabState.intValue != page) {
                mainTabState.intValue = page
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                userScrollEnabled = false,
                beyondViewportPageCount = 4
            ) { page ->
                pageContent(page)
            }
        }
    }

}
