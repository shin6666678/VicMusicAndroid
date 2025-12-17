package com.shin.vicmusic.feature.song

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val SONG_DETAIL_ROUTE="songDetail"

fun NavGraphBuilder.songDetailScreen() {
    composable(
        route = "$SONG_DETAIL_ROUTE/{songId}", // Define the route with the argument
        arguments = listOf(navArgument("songId") { type = NavType.StringType }) // Specify argument type
    ) {
        SongDetailRoute() // 传递 navController
    }
}

// 构建路由字符串的函数 (推荐创建一个扩展函数)
fun NavController.navigateToSongDetail(songId: String) {
    this.navigate("$SONG_DETAIL_ROUTE/$songId")
}