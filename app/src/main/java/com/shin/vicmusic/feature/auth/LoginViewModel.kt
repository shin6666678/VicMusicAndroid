package com.shin.vicmusic.feature.auth

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authViewModel: AuthViewModel // 注入AuthViewModel
) : ViewModel() {

    var _qrcodeAuthStatus = MutableStateFlow<Int?>(null)
    val qrcodeAuthStatus: StateFlow<Int?> = _qrcodeAuthStatus
    var _qrcodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrcodeBitmap: StateFlow<Bitmap?> = _qrcodeBitmap
    var _getAccountInfoSuccess = MutableStateFlow<Boolean?>(null)
    val getAccountInfoSuccess: StateFlow<Boolean?> = _getAccountInfoSuccess
    private var mLastQrcodeAuthJob: Job? = null
    private var mCookie = ""
    fun qrcodeAuth() {

    }
//    private suspend fun getAccountInfo() {
//        val accountInfoResult = api.getAccountInfo(mCookie)
//        if (accountInfoResult.resultOk()) {
//            val loginResult = LoginResult(accountInfoResult.account, accountInfoResult.profile, mCookie)
//            AppGlobalData.sLoginResult = loginResult
//            AppGlobalData.sLoginRefreshFlag = !AppGlobalData.sLoginRefreshFlag
//            getAccountInfoSuccess = true
//            authViewModel.setLoginStatus(true) // 登录成功，更新全局状态
//        } else {
//            getAccountInfoSuccess = false
//            authViewModel.setLoginStatus(false) // 登录失败，更新全局状态
//        }
//    }
}