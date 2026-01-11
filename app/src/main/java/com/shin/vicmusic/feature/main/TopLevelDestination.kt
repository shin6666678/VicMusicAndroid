package com.shin.vicmusic.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.shin.vicmusic.R
import com.shin.vicmusic.feature.discovery.DISCOVERY_ROUTE
import com.shin.vicmusic.feature.feed.FEED_ROUTE
import com.shin.vicmusic.feature.me.ME_ROUTE

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
    val route: String
) {
    // 首页 -> 使用“四格方块”，极简几何，代表聚合内容
    DISCOVERY(
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        titleTextId = R.string.tab_main,
        route = DISCOVERY_ROUTE
    ),
    // 我的 -> 使用“纯人像”，去掉外圈，更加清爽
    ME(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        titleTextId = R.string.tab_me,
        route = ME_ROUTE
    ),
    // 动态
    FEED(
        selectedIcon = Icons.Filled.People, // 对应之前的"朋友/动态"
        unselectedIcon = Icons.Outlined.People,
        titleTextId = R.string.tab_cart,
        route = FEED_ROUTE
    )
}