package com.shin.vicmusic.feature.me.fanList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val FAN_LIST_ROUTE = "fan_list"

fun NavController.navigateToFanList() {
    navigate(FAN_LIST_ROUTE)
}

fun NavGraphBuilder.fanListScreen() {
    composable(FAN_LIST_ROUTE) {
        FanListScreen()
    }
}