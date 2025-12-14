package com.shin.vicmusic.feature.me

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.feature.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val authManager: AuthManager // ✅ 这里可以正常注入你的单例 Manager
) : ViewModel() {

    // 直接透传 Manager 的状态给 UI 使用
    val isLoggedIn = authManager.isLoggedIn
    val currentUser = authManager.currentUser

    // 如果需要，也可以暴露方法
    // fun logout() = authManager.setLoginStatus(false)
    // [新增] 暴露获取用户信息的方法
    fun fetchUserInfo() {
        authManager.fetchUserInfo()
    }
}