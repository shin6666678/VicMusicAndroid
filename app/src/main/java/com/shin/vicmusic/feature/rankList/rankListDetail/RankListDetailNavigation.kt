package com.shin.vicmusic.feature.rankList.rankListDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RANK_LIST_DETAIL_ROUTE = "rankList_detail"
fun NavGraphBuilder.rankListDetailScreen() {
    composable(
        route = "$RANK_LIST_DETAIL_ROUTE/{rankListId}",
        arguments = listOf(
            navArgument("rankListId") { type = NavType.StringType }
        )
    ) {
        RankListDetailRoute()
    }
}

fun NavController.navigateToRankListDetail(rankListId: String) {
    this.navigate("$RANK_LIST_DETAIL_ROUTE/$rankListId")
}