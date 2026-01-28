package com.shin.vicmusic.feature.feed.publish

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// 1. 将路由定义为常量，作为此功能的单一事实来源
const val PUBLISH_FEED_ROUTE_BASE = "publish_feed"
const val PUBLISH_FEED_ROUTE = "$PUBLISH_FEED_ROUTE_BASE?targetId={targetId}&targetType={targetType}"

private const val TARGET_ID = "targetId"
private const val TARGET_TYPE = "targetType"

// 2. NavGraphBuilder 扩展，用于在 NavHost 中构建
fun NavGraphBuilder.publishFeedScreen() {
    composable(
        route = PUBLISH_FEED_ROUTE,
        arguments = listOf(
            navArgument(TARGET_ID) { type = NavType.StringType; nullable = true },
            navArgument(TARGET_TYPE) { type = NavType.StringType; nullable = true },
        )
    ) {
        PublishFeedRoute()
    }
}

// 3. NavController 扩展，用于从任何地方发起导航
fun NavController.navigateToPublishFeed(targetId: String? = null, targetType: String? = null) {
    val route = if (targetId != null && targetType != null) {
        "$PUBLISH_FEED_ROUTE_BASE?$TARGET_ID=$targetId&$TARGET_TYPE=$targetType"
    } else {
        PUBLISH_FEED_ROUTE_BASE
    }
    this.navigate(route)
}
