package com.shin.vicmusic.feature.me.setting

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    fun logout() {
        authManager.setLoginStatus(false)
    }
}