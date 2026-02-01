package com.shin.vicmusic.feature.me.setting

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.core.manager.AuthManager
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authManager: AuthManager,
    @ApplicationContext private val context: Context
) : ViewModel() {
    fun logout() {
        authManager.setLoginStatus(false)
    }

    fun triggerMessageCheck() {
        // 创建一次性任务请求
        val workRequest = OneTimeWorkRequestBuilder<com.shin.vicmusic.core.worker.MessageCheckWorker>()
            .build()
        // 立即加入队列
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}