package com.shin.vicmusic.feature.splash

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SystemRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.ApkInstaller
import com.shin.vicmusic.core.model.api.AppUpdateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val systemRepository: SystemRepository,
    private val apkInstaller: ApkInstaller
) : ViewModel() {

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _updateState = MutableStateFlow<AppUpdateDto?>(null)
    val updateState = _updateState.asStateFlow()

    //下载进度状态，-1 表示未开始，0-100 表示进度
    private val _downloadProgress = MutableStateFlow(-1)
    val downloadProgress = _downloadProgress.asStateFlow()

    val navigateToMain = MutableStateFlow(false)
    private var timer: CountDownTimer? = null

    init {
        checkVersionAndInit()
    }

    private fun checkVersionAndInit() {
        viewModelScope.launch {
            checkUpdate()
        }
    }
    private fun checkUpdate() {
        viewModelScope.launch {
            val result = systemRepository.checkAppUpdate()
            if (result is MyNetWorkResult.Success && result.data.hasUpdate) {
                _updateState.value = result.data
            } else {
                startTimer()
            }
        }
    }

    private fun startTimer(time: Long = 3000) {
        timer = object : CountDownTimer(time, 1000) {
            override fun onFinish() { toNext() }
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished / 1000 + 1
            }
        }.start()
    }

    private fun toNext() {
        navigateToMain.value = true
    }

    fun onSkipAdClick() {
        timer?.cancel()
        toNext()
    }

    fun onIgnoreUpdate() {
        _updateState.value = null
        toNext()
    }

    fun onConfirmUpdate() {
        // 防止重复点击
        if (_downloadProgress.value in 0..99) return

        val updateInfo = _updateState.value ?: return
        val url = updateInfo.downloadUrl ?: return

        viewModelScope.launch {
            apkInstaller.downloadAndInstall(url, "VicMusic 新版本")
                .catch { e ->
                    // 下载失败，重置进度，UI可以根据-1状态显示“下载失败”
                    _downloadProgress.value = -1
                    e.printStackTrace()
                }
                .collect { progress ->
                    // 收集并更新进度状态
                    _downloadProgress.value = progress
                }
        }
    }
}