package com.shin.vicmusic.feature.splash

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SystemRepository
import com.shin.vicmusic.core.manager.VersionManager
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.ApkInstaller
import com.shin.vicmusic.core.model.api.AppUpdateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val systemRepository: SystemRepository,
    private val versionManager: VersionManager,
    private val apkInstaller: ApkInstaller
) : ViewModel() {

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _updateState = MutableStateFlow<AppUpdateDto?>(null)
    val updateState = _updateState.asStateFlow()

    // 新增：版本更新说明弹窗状态
    private val _releaseNoteState = MutableStateFlow<String?>(null)
    val releaseNoteState = _releaseNoteState.asStateFlow()

    val navigateToMain = MutableStateFlow(false)
    private var timer: CountDownTimer? = null

    // 硬编码本次更新的内容，实际开发中也可以放在资源文件或服务端配置
    private val currentReleaseNotes = """
        本次更新内容：
        1. 修复搜索框问题
        2. 修复VIP界面展示问题
    """.trimIndent()

    init {
        checkVersionAndInit()
    }

    private fun checkVersionAndInit() {
        viewModelScope.launch {
            val currentCode = systemRepository.getCurrentVersionCode()
            val savedCode = versionManager.getSavedVersionCode()

            // 如果保存的版本号小于当前版本号，说明是更新后首次运行（或者是全新安装）
            if (savedCode < currentCode) {
                _releaseNoteState.value = currentReleaseNotes
            } else {
                // 非首次运行，正常检查更新
                checkUpdate()
            }
        }
    }

    // 用户确认看过更新说明
    fun onReleaseNoteConfirm() {
        viewModelScope.launch {
            // 保存当前版本号
            val currentCode = systemRepository.getCurrentVersionCode()
            versionManager.saveVersionCode(currentCode)

            // 关闭弹窗并继续原有流程（检查新版本或倒计时）
            _releaseNoteState.value = null
            checkUpdate()
        }
    }

    private fun checkUpdate() {
        viewModelScope.launch {
            // 检查更新
            val result = systemRepository.checkAppUpdate()
            if (result is Result.Success && result.data.hasUpdate) {
                // 有更新：暂停进入主页，显示弹窗
                _updateState.value = result.data
            } else {
                // 无更新：正常倒计时
                startTimer()
            }
        }
    }

    private fun startTimer(time: Long = 3000) {
        timer = object : CountDownTimer(time, 1000) {
            override fun onFinish() {
                toNext()
            }

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

    // 用户点击"暂不更新"
    fun onIgnoreUpdate() {
        _updateState.value = null
        toNext() // 直接进入
    }

    fun onConfirmUpdate() {
        // 获取当前的更新信息
        val updateInfo = _updateState.value
        if (updateInfo?.downloadUrl != null) {
            // 调用工具类下载
            apkInstaller.downloadAndInstall(updateInfo.downloadUrl)

            // 如果是强制更新，保持弹窗不关闭；如果是非强制，可以关闭弹窗或显示下载中
            if (!updateInfo.isForce) {
                // _updateState.value = null // 可选：关闭弹窗
            }
        }
    }
}