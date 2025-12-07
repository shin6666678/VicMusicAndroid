package com.shin.vicmusic.feature.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SEARCH_ROUTE = "search_route"

fun NavGraphBuilder.searchScreen(
    navController: NavController
) {
    composable(SEARCH_ROUTE) {
        SearchRoute(navController = navController)
    }
}

fun NavController.navigateToSearch() {
    navigate(SEARCH_ROUTE)
}
