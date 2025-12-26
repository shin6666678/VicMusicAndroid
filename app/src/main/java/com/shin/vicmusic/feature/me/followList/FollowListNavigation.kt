package com.shin.vicmusic.feature.me.followList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val FOLLOW_LIST_ROUTE = "follow_list"

// 跳转到关注列表 (Navigate to Follow List)
fun NavController.navigateToFollowList() {
    navigate(FOLLOW_LIST_ROUTE)
}

// 注册 Me 模块的路由图 (Register Route Graph for Me Module)
fun NavGraphBuilder.followListScreen() {
    // 注册关注列表页面 (Register Follow List Screen)
    composable(FOLLOW_LIST_ROUTE) {
        FollowListScreen()
    }
}