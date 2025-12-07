package com.shin.vicmusic.feature.register


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.login.LOGIN_ROUTE
import com.shin.vicmusic.feature.login.LoginRoute
import com.shin.vicmusic.feature.main.MAIN_ROUTE
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE
import com.shin.vicmusic.feature.splash.SplashRoute

const val REGISTER_ROUTE="register"

fun NavGraphBuilder.registerScreen(navController: NavController){
    composable(REGISTER_ROUTE){
        RegisterRoute(navController = navController)
    }
}

fun NavController.navigateToRegister():Unit{
    navigate(REGISTER_ROUTE){
        launchSingleTop=true
        popUpTo(SPLASH_ROUTE){
            inclusive=true
        }
    }
}