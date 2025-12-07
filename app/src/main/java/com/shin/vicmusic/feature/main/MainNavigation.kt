package com.shin.vicmusic.feature.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE

const val MAIN_ROUTE="main"

fun NavGraphBuilder.mainScreen(
    finishPage: () -> Unit,
    navController:NavHostController
) {
    composable(MAIN_ROUTE){
        MainRoute(finishPage = finishPage,navController=navController)
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