package com.shin.vicmusic.feature.liked

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val LIKED_LIST_ROUTE = "liked_list"
fun NavGraphBuilder.likedListScreen() {
    composable(LIKED_LIST_ROUTE) {
        LikedRoute()
    }
}

fun NavController.navigateToLikedList() {
    this.navigate(LIKED_LIST_ROUTE)
}