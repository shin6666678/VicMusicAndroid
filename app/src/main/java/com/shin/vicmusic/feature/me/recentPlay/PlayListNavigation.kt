package com.shin.vicmusic.feature.me.recentPlay

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RECENT_PLAY_ROUTE = "recent_play"

fun NavController.navigateToRecentPlay() {
    navigate(RECENT_PLAY_ROUTE)
}

fun NavGraphBuilder.playListScreen() {

    composable(RECENT_PLAY_ROUTE) {
        RecentPlayScreen()
    }
}