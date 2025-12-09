package com.shin.vicmusic.feature.likedSongs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.me.ME_ROUTE
import com.shin.vicmusic.feature.me.MeRoute

// 在你的 Navigation 文件中 (例如 MeNavigation.kt 或 MainNavigation.kt)

const val LIKED_SONGS_ROUTE = "liked_songs"

fun NavGraphBuilder.meGraph(
    navController: NavHostController,
    // ... 其他回调
) {
    // ... MeRoute 配置 ...
    composable(ME_ROUTE) {
        MeRoute(
            onLikedSongsClick = {
                navController.navigate(LIKED_SONGS_ROUTE) // 跳转
            },
            // ...
        )
    }

    // [新增] 注册喜欢列表页面
    composable(LIKED_SONGS_ROUTE) {
        LikedSongsRoute(
            onBackClick = { navController.popBackStack() }
        )
    }
}