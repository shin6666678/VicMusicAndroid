package com.shin.vicmusic.feature.playlist.daily

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val DAILY_PLAYLIST_ROUTE = "daily_playlist"

fun NavGraphBuilder.dailyPlaylistScreen() {
    composable(route = DAILY_PLAYLIST_ROUTE) {
        DailyPlaylistRoute()
    }
}

fun NavController.navigateToDailyPlaylist() {
    this.navigate(DAILY_PLAYLIST_ROUTE)
}
