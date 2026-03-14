package com.shin.vicmusic.feature.message

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val MESSAGE_LIST_ROUTE = "message_list"
const val FEED_NOTIFICATIONS_ROUTE = "feed_notifications"
fun NavGraphBuilder.messageListScreen() {
    composable(
        route = "message_list",
        deepLinks = listOf(
            navDeepLink { uriPattern = "vicmusic://messages" }
        )
    ) {
        MessageListRoute()
    }
    composable(FEED_NOTIFICATIONS_ROUTE) {
        FeedNotifyRoute()
    }
}

fun NavController.navigateToMessageList() {
    this.navigate(MESSAGE_LIST_ROUTE)
}