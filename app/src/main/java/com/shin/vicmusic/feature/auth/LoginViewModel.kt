package com.shin.vicmusic.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authViewModel: AuthViewModel,
    private val datasource: MyRetrofitDatasource // [新增] 注入 API
) : ViewModel() {

    // UI State
    private val _mail = MutableStateFlow("")
    val mail: StateFlow<String> = _mail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun updateMail(input: String) {
        _mail.value = input
    }

    fun updatePassword(input: String) {
        _password.value = input
    }

    fun login() {
        if (_mail.value.isBlank() || _password.value.isBlank()) {
            _loginState.value = LoginUiState.Error("账号或密码不能为空")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val req = UserLoginReq(mail = _mail.value, pwd = _password.value)
                val response = datasource.login(req)

                if (response.status == 0) { // 假设 0 是成功码，请根据实际后端调整
                    // 登录成功
                    // [New] 保存 Token
                    AppGlobalData.token = response.data.toString()
                    Log.d("LoginViewModel", "Login Success: ${response.data}")
                    authViewModel.setLoginStatus(true)
                    _loginState.value = LoginUiState.Success
                } else {
                    _loginState.value = LoginUiState.Error(response.message ?: "登录失败")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginState.value = LoginUiState.Error("网络错误: ${e.message}")
            }
        }
    }
}

// 简单的 UI 状态类
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}