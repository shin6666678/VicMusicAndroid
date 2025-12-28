package com.shin.vicmusic.feature.checkIn

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val CHECK_IN_ROUTE = "check_in_route"

fun NavController.navigateToCheckIn() {
    this.navigate(CHECK_IN_ROUTE)
}

fun NavGraphBuilder.checkInScreen(
) {
    composable(route = CHECK_IN_ROUTE) {
        CheckInRoute()
    }
}