package com.shin.vicmusic.feature.splash

import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.concurrent.timer

/**
 * 启动界面VM
 */
class SplashViewModel: ViewModel() {
    private val _timeLeft= MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft
    val navigateToMain=MutableStateFlow(false)
    private var timer : CountDownTimer? = null
    init {
        delayToNext()
    }

    private fun delayToNext(time:Long =3000) {
        timer=object :CountDownTimer(time,1000){
            override fun onFinish() {
                toNext()
            }

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value=millisUntilFinished/1000+1
            }
        }.start()
    }

    private fun toNext() {
        navigateToMain.value=true
    }
     fun onSkipAdClick(){
         timer?.cancel()
         toNext()
    }
}