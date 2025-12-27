package com.shin.vicmusic.feature.myInfo

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MY_INFO_ROUTE = "my_info"
fun NavGraphBuilder.myInfoScreen() {
    composable(MY_INFO_ROUTE) {
        MyInfoRoute()
    }
}

fun NavController.navigateToMyInfo() {
    this.navigate(MY_INFO_ROUTE)
}