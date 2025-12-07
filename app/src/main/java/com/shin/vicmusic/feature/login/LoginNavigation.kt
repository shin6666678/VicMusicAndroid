package com.shin.vicmusic.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.main.MAIN_ROUTE
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE
import com.shin.vicmusic.feature.splash.SplashRoute

const val LOGIN_ROUTE="login"

fun NavGraphBuilder.loginScreen(navController: NavController){
    composable(LOGIN_ROUTE){
        LoginRoute(navController = navController)
    }
}

fun NavController.navigateToLogin():Unit{
    navigate(LOGIN_ROUTE){
        launchSingleTop=true
        popUpTo(SPLASH_ROUTE){
            inclusive=true
        }
    }
}