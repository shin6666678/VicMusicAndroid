package com.shin.vicmusic.feature.me.setting

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTING_ROUTE = "setting_route"

fun NavController.navigateToSetting() {
    navigate(SETTING_ROUTE)
}

fun NavGraphBuilder.settingScreen() {
    composable(
        route = SETTING_ROUTE,
        // 进入动画：从屏幕右侧滑入 (initialOffsetX = Full Width -> 0)
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            )
        },
        // 退出动画（当从设置页返回时）：向右滑出 (0 -> Full Width)
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            )
        }
    ) {
        SettingRoute()
    }
}