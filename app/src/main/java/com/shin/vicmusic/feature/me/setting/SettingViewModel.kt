package com.shin.vicmusic.feature.me.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.shin.vicmusic.core.manager.AppThemeMode
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val themeManager: ThemeManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val currentThemeMode = themeManager.themeMode

    fun updateTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }

    fun logout() {
        authManager.setLoginStatus(false)
    }

    fun triggerMessageCheck() {
        val workRequest = OneTimeWorkRequestBuilder<com.shin.vicmusic.core.worker.MessageCheckWorker>()
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}