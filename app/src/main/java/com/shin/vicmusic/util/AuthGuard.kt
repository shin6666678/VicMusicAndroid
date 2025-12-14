package com.shin.vicmusic.util

import androidx.navigation.NavController
import com.shin.vicmusic.feature.auth.AuthManager

import com.shin.vicmusic.feature.auth.navigateToLogin

/**
 * 检查登录状态：
 * - 已登录：执行 [block]
 * - 未登录：跳转到登录页
 */
fun NavController.checkLoginAndRun(
    authManager: AuthManager,
    block: () -> Unit
) {
    if (authManager.isLoggedIn.value == true) {
        block()
    } else {
        this.navigateToLogin()
    }
}