package com.shin.vicmusic.feature.myInfo.edit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MY_INFO_EDIT_ROUTE = "my_info_edit"

fun NavGraphBuilder.myInfoEditScreen() {
    composable(route = MY_INFO_EDIT_ROUTE) {
        MyInfoEditRoute()
    }
}

fun NavController.navigateToMyInfoEdit() {
    this.navigate(MY_INFO_EDIT_ROUTE)
}
