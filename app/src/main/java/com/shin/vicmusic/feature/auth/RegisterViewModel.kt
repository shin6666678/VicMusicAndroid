package com.shin.vicmusic.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AuthRepository
import com.shin.vicmusic.core.model.request.UserRegisterReq
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
sealed class RegisterIntent {
    data class UpdateEmail(val email: String) : RegisterIntent()
    data class UpdateCaptcha(val captcha: String) : RegisterIntent()
    data class UpdatePassword(val password: String) : RegisterIntent()
    data class UpdateMailCode(val code: String) : RegisterIntent()
    object RefreshCaptcha : RegisterIntent()
    object SendEmailCode : RegisterIntent()
    object Register : RegisterIntent()
}

// ============ MVI：State（统一UI状态）============
data class RegisterUiState(
    val email: String = "",
    val captcha: String = "",
    val password: String = "",
    val mailCode: String = "",
    val captchaImageUrl: String? = null,
    val isLoading: Boolean = false,
    val emailCodeSent: Boolean = false,      // 验证码已成功发送
    val emailCodeSending: Boolean = false,   // 发送中
    val errorMessage: String? = null
)

// ============ MVI：Effect（一次性副作用）============
sealed class RegisterEffect {
    object NavigateBack : RegisterEffect()
    data class ShowToast(val message: String) : RegisterEffect()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val TAG = "RegisterViewModel"
    private var captchaCounter = 0

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    init {
        // 初始加载图形验证码
        processIntent(RegisterIntent.RefreshCaptcha)
    }

    // ============ MVI：单一Intent入口 ============
    fun processIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.UpdateEmail -> _uiState.update { it.copy(email = intent.email) }
            is RegisterIntent.UpdateCaptcha -> _uiState.update { it.copy(captcha = intent.captcha) }
            is RegisterIntent.UpdatePassword -> _uiState.update { it.copy(password = intent.password) }
            is RegisterIntent.UpdateMailCode -> _uiState.update { it.copy(mailCode = intent.code) }
            is RegisterIntent.RefreshCaptcha -> refreshCaptcha()
            is RegisterIntent.SendEmailCode -> sendEmailCode()
            is RegisterIntent.Register -> register()
        }
    }

    private fun refreshCaptcha() {
        captchaCounter++
        Log.d(TAG, "刷新验证码，第 $captchaCounter 次")
        _uiState.update {
            it.copy(captchaImageUrl = "http://115.190.155.131:9001/api/notify/v1/captcha/$captchaCounter")
        }
    }

    private fun sendEmailCode() {
        val state = _uiState.value
        if (state.email.isBlank() || state.captcha.isBlank()) {
            viewModelScope.launch { _effect.emit(RegisterEffect.ShowToast("请填写邮箱和图形验证码")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(emailCodeSending = true, errorMessage = null) }
            Log.d(TAG, "发送验证码: ${state.email}, ${state.captcha}")
            try {
                val result = authRepository.mailCode(state.email, state.captcha)
                if (result.code == 0) {
                    _uiState.update { it.copy(emailCodeSending = false, emailCodeSent = true) }
                    _effect.emit(RegisterEffect.ShowToast("验证码已发送，请查收邮件"))
                } else {
                    _uiState.update { it.copy(emailCodeSending = false, errorMessage = result.message ?: "发送失败") }
                    _effect.emit(RegisterEffect.ShowToast(result.message ?: "发送失败，请重试"))
                    refreshCaptcha() // 发送失败自动刷新验证码
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "网络错误: ${e.message}"
                _uiState.update { it.copy(emailCodeSending = false, errorMessage = msg) }
                _effect.emit(RegisterEffect.ShowToast(msg))
                refreshCaptcha()
            }
        }
    }

    private fun register() {
        val state = _uiState.value
        if (state.mailCode.isBlank() || state.password.isBlank()) {
            viewModelScope.launch { _effect.emit(RegisterEffect.ShowToast("请填写邮箱验证码和密码")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            Log.d(TAG, "注册: ${state.email}, ${state.mailCode}, ${state.password}")
            try {
                val result = authRepository.register(
                    UserRegisterReq(
                        name = "123",
                        pwd = state.password,
                        headImg = "",
                        mail = state.email,
                        code = state.mailCode,
                    )
                )
                if (result.code == 0) {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.emit(RegisterEffect.ShowToast("注册成功！"))
                    _effect.emit(RegisterEffect.NavigateBack)
                } else {
                    val msg = result.message ?: "注册失败"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _effect.emit(RegisterEffect.ShowToast(msg))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "网络错误: ${e.message}"
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                _effect.emit(RegisterEffect.ShowToast(msg))
            }
        }
    }
}
