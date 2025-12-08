package com.shin.vicmusic.feature.auth


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE

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