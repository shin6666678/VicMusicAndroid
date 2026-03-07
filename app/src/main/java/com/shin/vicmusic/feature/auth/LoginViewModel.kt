package com.shin.vicmusic.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.data.repository.AuthRepository
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.TokenManager
import com.shin.vicmusic.core.model.request.UserLoginReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============ MVI：Intent（用户意图）============
sealed class LoginIntent {
    data class UpdateMail(val mail: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object Login : LoginIntent()
}

// ============ MVI：State（统一UI状态）============
data class LoginUiState(
    val mail: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// ============ MVI：Effect（一次性副作用）============
sealed class LoginEffect {
    object NavigateToMain : LoginEffect()
    data class ShowToast(val message: String) : LoginEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    // ============ MVI：单一Intent入口 ============
    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateMail -> _uiState.update { it.copy(mail = intent.mail) }
            is LoginIntent.UpdatePassword -> _uiState.update { it.copy(password = intent.password) }
            is LoginIntent.Login -> login()
        }
    }

    private fun login() {
        val state = _uiState.value
        if (state.mail.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "账号或密码不能为空") }
            viewModelScope.launch { _effect.emit(LoginEffect.ShowToast("账号或密码不能为空")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val req = UserLoginReq(mail = state.mail, pwd = state.password)
                val response = authRepository.login(req)

                if (response.code == 0) {
                    val token = response.data.toString().trim('"')
                    AppGlobalData.token = token
                    tokenManager.saveToken(token)
                    Log.d("LoginViewModel", "Login Success: ${response.data}")
                    authManager.setLoginStatus(true)
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(LoginEffect.NavigateToMain)
                } else {
                    val msg = response.message ?: "登录失败"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _effect.emit(LoginEffect.ShowToast(msg))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "网络错误: ${e.message}"
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                _effect.emit(LoginEffect.ShowToast(msg))
            }
        }
    }
}