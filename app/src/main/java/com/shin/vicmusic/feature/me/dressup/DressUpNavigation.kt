package com.shin.vicmusic.feature.me.dressup

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.dressUpScreen(popBackStack: () -> Unit) {
    composable("dress_up") {
        DressUpRoute(popBackStack = popBackStack)
    }
}

fun NavController.navigateToDressUp() {
    navigate("dress_up")
}
