package com.shin.vicmusic.feature.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 更改为 @Singleton
class AuthViewModel @Inject constructor() : ViewModel() {

    // true 表示已登录，false 表示未登录，null 表示尚未确定
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // 供LoginViewModel或其他认证流程调用，以更新全局登录状态
    fun setLoginStatus(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }
}