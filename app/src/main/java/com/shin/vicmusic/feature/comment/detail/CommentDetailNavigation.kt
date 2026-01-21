package com.shin.vicmusic.feature.comment.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val COMMENT_DETAIL_ROUTE = "comment_detail"

fun NavGraphBuilder.commentDetailScreen() {
    composable(
        route = "$COMMENT_DETAIL_ROUTE/{commentId}/{resourceId}/{resourceType}",
        arguments = listOf(
            navArgument("commentId") { type = NavType.StringType },
            navArgument("resourceId") { type = NavType.StringType },
            navArgument("resourceType") { type = NavType.StringType }
        )
    ) {
        CommentDetailRoute()
    }
}

fun NavController.navigateToCommentDetail(commentId: String, resourceId: String, resourceType: String) {
    this.navigate("$COMMENT_DETAIL_ROUTE/$commentId/$resourceId/$resourceType")
}
