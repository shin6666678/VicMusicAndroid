package com.shin.vicmusic.feature.playlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MY_PLAYLISTS_ROUTE = "my_playlists"

/**
 * 提供给 NavGraphBuilder 的扩展函数，用于注册界面
 */
fun NavGraphBuilder.myPlaylistScreen(
) {
    composable(route = MY_PLAYLISTS_ROUTE) {
        MyPlaylistRoute()
    }
}

/**
 * 提供给 NavController 的扩展函数，用于跳转
 */
fun NavController.navigateToMyPlaylists(navOptions: NavOptions? = null) {
    this.navigate(MY_PLAYLISTS_ROUTE, navOptions)
}