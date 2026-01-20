package com.shin.vicmusic.feature.feed

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val FEED_ROUTE = "feed"

fun NavController.navigateToFeed(navOptions: NavOptions? = null) {
    this.navigate(FEED_ROUTE, navOptions)
}

fun NavGraphBuilder.feedScreen() {
    composable(route = FEED_ROUTE) {
        FeedRoute()
    }
}
