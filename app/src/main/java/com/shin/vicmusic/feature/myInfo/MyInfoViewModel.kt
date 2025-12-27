package com.shin.vicmusic.feature.myInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    val currentUser = authManager.currentUser
    val isLoggedIn = authManager.isLoggedIn

    fun refresh() {
        authManager.fetchUserInfo()
    }

    fun logout() {
        authManager.setLoginStatus(false)
    }
}