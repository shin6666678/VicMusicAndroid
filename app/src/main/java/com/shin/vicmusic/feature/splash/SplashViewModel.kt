package com.shin.vicmusic.feature.splash

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SystemRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.api.AppUpdateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val systemRepository: SystemRepository
) : ViewModel() {

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _updateState = MutableStateFlow<AppUpdateDto?>(null)
    val updateState = _updateState.asStateFlow()

    val navigateToMain = MutableStateFlow(false)
    private var timer: CountDownTimer? = null

    init {
        checkUpdate()
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
}