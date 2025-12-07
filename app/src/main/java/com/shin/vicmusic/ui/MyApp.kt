package com.shin.vicmusic.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.login.LOGIN_ROUTE
import com.shin.vicmusic.feature.login.loginScreen
import com.shin.vicmusic.feature.login.navigateToLogin
import com.shin.vicmusic.feature.main.mainScreen
import com.shin.vicmusic.feature.main.navigateToMain
import com.shin.vicmusic.feature.register.registerScreen
import com.shin.vicmusic.feature.song.songDetailScreen
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE
import com.shin.vicmusic.feature.splash.splashScreen
import com.shin.vicmusic.feature.search.searchScreen // 导入 searchScreen

@Composable
fun MyApp(navController: NavHostController) {
    NavHost(navController=navController, startDestination = SPLASH_ROUTE){
        splashScreen(toMain = navController::navigateToMain, toLogin = navController::navigateToLogin)
        mainScreen(finishPage = navController::popBackStack,navController=navController)
        loginScreen(navController) // 添加 loginScreen 作为目的地
        songDetailScreen(navController = navController) // 添加歌曲详情页的路由定义
        registerScreen(navController = navController)
        searchScreen(navController = navController) // 添加 searchScreen 作为目的地
    }
}
