package com.shin.vicmusic.feature.vip

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val VIP_ROUTE = "vip"

fun NavController.navigateToVip() {
    navigate(VIP_ROUTE)
}

fun NavGraphBuilder.vipScreen(onBackClick: () -> Unit) {
    composable(VIP_ROUTE) {
        VipRoute(onBackClick = onBackClick)
    }
}