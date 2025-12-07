package com.shin.vicmusic.feature.main

import com.shin.vicmusic.R
import com.shin.vicmusic.feature.discovery.DISCOVERY_ROUTE
import com.shin.vicmusic.feature.feed.FEED_ROUTE
import com.shin.vicmusic.feature.me.ME_ROUTE
import com.shin.vicmusic.feature.shortVideo.SHORT_VIDEO_ROUTE

enum class TopLevelDestination (
    val selectedIcon: Int,
    val unselectedIcon:Int,
    val titleTextId:Int,
    val route: String
){
    //发现界面
    DISCOVERY(
        selectedIcon = R.drawable.ic_disc,
        unselectedIcon = R.drawable.ic_discovery,
        titleTextId = R.string.tab_main,
        route = DISCOVERY_ROUTE
    ),
    SHORT_VIDEO(
        selectedIcon = R.drawable.ic_disc,
        unselectedIcon = R.drawable.ic_discovery,
    titleTextId = R.string.tab_video,
    route = SHORT_VIDEO_ROUTE
    ),
    ME(
    selectedIcon = R.drawable.ic_disc,
    unselectedIcon = R.drawable.ic_discovery,
    titleTextId = R.string.tab_me,
    route = ME_ROUTE
    ),
    FEED(
    selectedIcon = R.drawable.ic_disc,
    unselectedIcon = R.drawable.ic_discovery,
    titleTextId = R.string.tab_cart,
    route = FEED_ROUTE
    )
}