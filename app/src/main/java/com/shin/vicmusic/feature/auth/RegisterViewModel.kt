package com.shin.vicmusic.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource // [新增] 注入 API
) : ViewModel() {
    val TAG = "RegisterViewModel"

    var flashTimes=0;

    // 用户输入的邮箱
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // 用户输入的图形验证码
    private val _captcha = MutableStateFlow("")
    val captcha: StateFlow<String> = _captcha.asStateFlow()

    // 用户输入的密码
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    // 用户输入的邮箱验证码
    private val _mailCode = MutableStateFlow("")
    val mailCode: StateFlow<String> = _mailCode.asStateFlow()

    // 图形验证码图片 URL
    private val _captchaImageUrl = MutableStateFlow<String?>(null)
    val captchaImageUrl: StateFlow<String?> = _captchaImageUrl.asStateFlow()

    // 邮箱验证码发送状态 (null: 初始/加载中, true: 成功, false: 失败)
    private val _sendEmailCodeStatus = MutableStateFlow<Boolean?>(null)
    val sendEmailCodeStatus: StateFlow<Boolean?> = _sendEmailCodeStatus.asStateFlow()


    // 更新邮箱输入
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    // 更新图形验证码输入
    fun updateCaptcha(newCaptcha: String) {
        _captcha.value = newCaptcha
    }

    // 更新密码输入
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }
    // 更新邮箱验证码输入
    fun updateMailCode(newMailCode: String) {
        _mailCode.value = newMailCode
    }

    // 获取图形验证码图片
    fun fetchCaptchaImage() {
        viewModelScope.launch {
            Log.d(TAG, "获取验证码图片")
            flashTimes++
            _captchaImageUrl.value = "http://115.190.155.131:9001/api/notify/v1/captcha/$flashTimes"
        }
    }

    // 发送邮箱验证码
    fun requestEmailVerificationCode(mail: String, code: String) {
        viewModelScope.launch {
            Log.d(TAG, "发送验证码: ${mail},${code}")
            val sendMailCodeResult = datasource.mailCode(mail, code)
            _sendEmailCodeStatus.value = sendMailCodeResult.status == 0
        }
    }

    //注册
    fun register(mail: String, mailCode: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "注册按钮点击: ${mail},${mailCode},${password}")
            val sendMailCodeResult = datasource.register(
                UserRegisterReq(
                    name = "123",
                    pwd = password,
                    headImg = "",
                    mail = mail,
                    code = mailCode,
                )
            )
            _sendEmailCodeStatus.value = sendMailCodeResult.status == 0
        }
    }
}

