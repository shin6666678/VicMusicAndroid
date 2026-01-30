package com.shin.vicmusic.feature.myInfo

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import androidx.navigation.NavType
import androidx.navigation.navArgument

const val MY_INFO_ROUTE = "my_info"
const val USER_ID_ARG = "userId"
const val MY_INFO_WITH_ID_ROUTE = "$MY_INFO_ROUTE/{$USER_ID_ARG}"

fun NavGraphBuilder.myInfoScreen() {
    composable(
        route = MY_INFO_WITH_ID_ROUTE,
        arguments = listOf(
            navArgument(USER_ID_ARG) {
                type = NavType.StringType
            }
        )
    ) {
        MyInfoRoute()
    }
    composable(MY_INFO_ROUTE) {
        MyInfoRoute()
    }
}

fun NavController.navigateToMyInfo(userId: String? = null) {
    if (userId != null) {
        this.navigate("$MY_INFO_ROUTE/$userId")
    } else {
        this.navigate(MY_INFO_ROUTE)
    }
}