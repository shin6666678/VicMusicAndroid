package com.shin.vicmusic.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 更改为 @Singleton
class AuthViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource // [新增] 注入数据源
) : ViewModel() {

    // true 表示已登录，false 表示未登录，null 表示尚未确定
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // [新增] 当前用户信息状态 (Current User State)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // 供LoginViewModel或其他认证流程调用，以更新全局登录状态
    fun setLoginStatus(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
        if (loggedIn) {
            fetchUserInfo() // [新增] 登录成功自动获取用户信息
        } else {
            _currentUser.value = null // 登出清空信息
        }
    }

    // [新增] 获取用户信息方法
    fun fetchUserInfo() {
        viewModelScope.launch {
            val response = datasource.userInfo()
            if (response.status == 0) {
                _currentUser.value = response.data
                // 确保登录状态为 true
                if (_isLoggedIn.value != true) _isLoggedIn.value = true
            }
        }
    }
}