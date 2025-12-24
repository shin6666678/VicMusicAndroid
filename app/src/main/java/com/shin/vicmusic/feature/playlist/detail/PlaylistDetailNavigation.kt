package com.shin.vicmusic.feature.playlist.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val PLAYLIST_DETAIL_ROUTE = "playlist_detail"
internal const val PLAYLIST_ID_ARG = "id"

fun NavGraphBuilder.playlistDetailScreen() {
    composable(
        route = "$PLAYLIST_DETAIL_ROUTE/{$PLAYLIST_ID_ARG}",
        arguments = listOf(
            navArgument(PLAYLIST_ID_ARG) { type = NavType.StringType }
        )
    ) {
        // 同样调用 Route
        PlaylistDetailRoute()
    }
}

fun NavController.navigateToPlaylistDetail(playlistId: String) {
    this.navigate("$PLAYLIST_DETAIL_ROUTE/$playlistId")
}