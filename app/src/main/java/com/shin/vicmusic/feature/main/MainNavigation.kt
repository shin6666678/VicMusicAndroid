package com.shin.vicmusic.feature.main

import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE

const val MAIN_ROUTE="main"

fun NavGraphBuilder.mainScreen(
    mainTabState: MutableIntState
) {
    composable(MAIN_ROUTE){
        MainRoute(
            mainTabState = mainTabState
        )
    }
}
fun NavController.navigateToMain():Unit{
    navigate(MAIN_ROUTE){
        launchSingleTop=true
        popUpTo(SPLASH_ROUTE){
            inclusive=true
        }
    }
}