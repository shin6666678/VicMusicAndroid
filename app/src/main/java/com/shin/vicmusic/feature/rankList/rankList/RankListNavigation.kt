package com.shin.vicmusic.feature.rankList.rankList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RANK_LIST_ROUTE = "rank_list"
fun NavGraphBuilder.rankListScreen() {
    composable(RANK_LIST_ROUTE) {
        RankListRoute()
    }
}

fun NavController.navigateToRankList() {
    this.navigate(RANK_LIST_ROUTE)
}